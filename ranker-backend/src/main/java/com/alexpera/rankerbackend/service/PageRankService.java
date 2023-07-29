package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.model.User;
import com.alexpera.rankerbackend.dao.model.UsersMedia;
import com.alexpera.rankerbackend.dao.model.UsersMediaId;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.exceptions.EmptyGraphException;
import com.alexpera.rankerbackend.exceptions.LoopException;
import com.alexpera.rankerbackend.model.anilist.EdgeGraph;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

// todo add logs for every method
@Component
public class PageRankService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UsersMediaRepository usersMediaRepository;
    @Autowired
    EdgeRepository edgeRepository;
    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    AnilistService anilistService;

    public PageRankService(UserRepository userRepository, UsersMediaRepository usersMediaRepository, EdgeRepository edgeRepository, MediaRepository mediaRepository, AnilistService anilistService) {
        this.userRepository = userRepository;
        this.usersMediaRepository = usersMediaRepository;
        this.edgeRepository = edgeRepository;
        this.mediaRepository = mediaRepository;
        this.anilistService = anilistService;
    }

    @Getter
    private final HashMap<String, Graph<RankedMedia, DefaultEdge>> usersGraph = new HashMap<>();

    static final double DAMPING_FACTOR = 0.85;

    private Graph<RankedMedia, DefaultEdge> getGraph(String username) {
        usersGraph.computeIfAbsent(username, k -> new DefaultDirectedGraph<>(DefaultEdge.class));
        return usersGraph.get(username);
    }

    public void add(String username, RankedMedia item) {
        if (!getGraph(username).containsVertex(item)) {
            getGraph(username).addVertex(item);
        }
    }

    public void addAll(String username, List<RankedMedia> items) {
        items.forEach(anilistMedia -> add(username, anilistMedia));
    }

    public void addLink(String username, Media worse, Media better) {
        if (worse.equals(better)) {
            throw new LoopException("Cannot add link to itself.");
        }
        getGraph(username).addEdge(worse.toRankedMedia(), better.toRankedMedia());
    }

    public void calculateIteration(String username) {
        HashMap<RankedMedia, Double> newValues = new HashMap<>();
        Graph<RankedMedia, DefaultEdge> userGraph = getGraph(username);
        for (RankedMedia item : userGraph.vertexSet()) {
            newValues.put(item, calculatePageRankOfMedia(userGraph, newValues, item));
        }

        for (RankedMedia item : userGraph.vertexSet()) {
            item.setPageRankValue(newValues.get(item));
        }
    }

    private double calculatePageRankOfMedia(Graph<RankedMedia, DefaultEdge> userGraph, HashMap<RankedMedia, Double> pageRankValues, RankedMedia vertex) {
        AtomicReference<Double> accumulator = new AtomicReference<>(0.0);
        userGraph.incomingEdgesOf(vertex).forEach(edge -> {
            RankedMedia node = userGraph.getEdgeSource(edge);
            accumulator.set(accumulator.get() + pageRankValues.get(node) / userGraph.outgoingEdgesOf(node).size());
        });
        return (1 - DAMPING_FACTOR) / userGraph.vertexSet().size() + accumulator.get() * DAMPING_FACTOR;
    }

    public Set<RankedMedia> getItems(String username) {
        return getGraph(username).vertexSet();
    }

    public List<RankedMedia> getItemsSorted(String username) {
        return getGraph(username).vertexSet().stream().sorted().toList();
    }

    public Set<Media> getNextComparison(String username) throws EmptyGraphException {
        // find item with lowest number of incoming or outgoing edges
        RankedMedia minItem = null;
        int minEdges = Integer.MAX_VALUE;
        for (RankedMedia item : getGraph(username).vertexSet()) {
            int edges = getGraph(username).inDegreeOf(item) + getGraph(username).outDegreeOf(item);
            if (edges < minEdges) {
                minEdges = edges;
                minItem = item;
            }
        }

        // get mid item
        List<RankedMedia> items = getItemsSorted(username);
        RankedMedia midItem = items.get(items.size() / 2);

        if (minItem == null) {
            throw new EmptyGraphException("No items in graph for user " + username + " to compare.");
        }
        return Set.of(minItem, midItem);
    }

    public Set<EdgeGraph<RankedMedia>> getEdges(String username) {
        Set<DefaultEdge> edges = getGraph(username).edgeSet();
        HashSet<EdgeGraph<RankedMedia>> result = new HashSet<>();
        for (DefaultEdge edge : edges) {
            result.add(new EdgeGraph<>(getGraph(username).getEdgeSource(edge), getGraph(username).getEdgeTarget(edge)));
        }
        return result;
    }

    public Set<DefaultEdge> loadUser(String username) {
        return loadUser(username, true);
    }

    public Set<DefaultEdge> loadUser(String username, boolean mergeAnilist) {
        if (usersGraph.containsKey(username)) {
            return new HashSet<>();
        }

        Graph<RankedMedia, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        loadVertex(username, mergeAnilist, graph);
        loadEdges(username, graph);

        usersGraph.put(username, graph); // todo low priority: add compatibility for concurrency
        return graph.edgeSet();
    }

    private void loadEdges(String username, Graph<RankedMedia, DefaultEdge> graph) {
        edgeRepository.findByUsername(username).forEach(edge -> graph
                .addEdge(edge.getBetter().toRankedMedia(),edge.getWorse().toRankedMedia()));
    }

    private void loadVertex(String username, boolean mergeAnilist, Graph<RankedMedia, DefaultEdge> graph) {
        usersMediaRepository.findAllByUserId(username).forEach(
                usersMedia -> {
                    RankedMedia rankedMedia = usersMedia.getMedia().toRankedMedia(usersMedia.getPagerankValue());
                    graph.addVertex(rankedMedia);
                }
        );
        AtomicReference<Boolean> newItemsFound = new AtomicReference<>(true);
        if (mergeAnilist) {
            List<Media> anilistItems = anilistService.retrieveCompletedMedia(username);

            anilistItems.forEach(
                    media -> {
                        RankedMedia rankedMedia = media.toRankedMedia();
                        if(!newItemsFound.get() && mediaRepository.findById(rankedMedia.getId()).isPresent()) {
                            newItemsFound.set(true);
                        }
                        graph.addVertex(rankedMedia);
                    }
            );
            saveMediaToDB(anilistItems);
        }

        if (newItemsFound.get()) {
            resetPageRankToInitialValue(graph);
        }
    }

    private void saveMediaToDB(List<Media> items) {
        mediaRepository.saveAll(items);
    }

    private void saveMediaOfUser(String username) { // todo should get called before shutdown
        User user = userRepository.findById(username);
        usersGraph.get(username).vertexSet().forEach(media -> {
            UsersMediaId id = new UsersMediaId(user.getId(), media.getId());
            usersMediaRepository.save(new UsersMedia(id, user, media, media.getPageRankValue()));
        });

    }

    private void resetPageRankToInitialValue(Graph<RankedMedia, DefaultEdge> graph) {
        double countVertex = (double) graph.vertexSet().size();
        graph.vertexSet().forEach(v -> v.setPageRankValue(1/countVertex));
    }

}
