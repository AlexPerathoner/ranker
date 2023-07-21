package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.model.UsersMedia;
import com.alexpera.rankerbackend.dao.model.UsersMediaId;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.Edge;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import com.alexpera.rankerbackend.model.anilist.VotedMedia;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
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
        if (!usersGraph.containsKey(username)) {
            usersGraph.put(username, new DefaultDirectedGraph<>(DefaultEdge.class));
        }
        return usersGraph.get(username);
    }

    public void add(String username, RankedMedia item) {
        if (!getGraph(username).containsVertex(item)) {
            getGraph(username).addVertex(item.toRankedMedia());
        }
    }

    public void addAll(String username, ArrayList<RankedMedia> items) {
        items.forEach(anilistMedia -> add(username, anilistMedia));
    }

    public void addLink(String username, Media better, Media worse) {
        // todo check for cycles, remove them
        getGraph(username).addEdge(worse.toRankedMedia(), better.toRankedMedia());
    }

    public void calculateIteration(String username) {
//        HashMap<RankedMedia, Double> newValues = new HashMap<>();
//        for (Media item : getGraph(username).vertexSet()) {
//            double sum = 0;
//            for (DefaultEdge edge : getGraph(username).incomingEdgesOf(item)) {
//                Media other = getGraph(username).getEdgeSource(edge);
//                sum += other.getPageRankValue() / getGraph(username).outDegreeOf(other);
//            }
//            newValues.put(item, (1 - DAMPING_FACTOR) / getGraph(username).vertexSet().size() + DAMPING_FACTOR * sum);
//        }
//
//        for (Media item : getGraph(username).vertexSet()) {
//            item.setPageRankValue(newValues.get(item));
//        }
    }

    public Set<RankedMedia> getItems(String username) {
        return getGraph(username).vertexSet();
    }

    public List<RankedMedia> getItemsSorted(String username) {
        return getGraph(username).vertexSet().stream().sorted().toList();
    }

    public List<VotedMedia> getItemsVoted(String username, DistributionFunction distribution) {
        List<RankedMedia> items = getItemsSorted(username);
        List<VotedMedia> votedItems = new ArrayList<>();
        for (RankedMedia item : items) {
            double vote;
            if (distribution == DistributionFunction.constant) {
                vote = (items.size() - items.indexOf(item)) / (double) items.size();
                votedItems.add(new VotedMedia(item, vote));
            }
        }
        return votedItems.stream().sorted((a, b) -> Double.compare(b.getVote(), a.getVote())).toList();
    }

    public Set<Media> getNextComparison(String username) throws RuntimeException {
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
            throw new RuntimeException("No items in graph");
        }
        return Set.of(minItem, midItem);
    }

    public Set<Edge<RankedMedia>> getEdges(String username) {
        Set<DefaultEdge> edges = getGraph(username).edgeSet();
        HashSet<Edge<RankedMedia>> result = new HashSet<>();
        for (DefaultEdge edge : edges) {
            result.add(new Edge<>(getGraph(username).getEdgeSource(edge), getGraph(username).getEdgeTarget(edge)));
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
        edgeRepository.findByUsername(username).forEach(edge -> {
            graph.addEdge(edge.getBetter().toRankedMedia(), edge.getWorse().toRankedMedia());
        });
    }

    private void loadVertex(String username, boolean mergeAnilist, Graph<RankedMedia, DefaultEdge> graph) {
        usersMediaRepository.findAllByUserId(username).forEach(
                usersMedia -> {
                    RankedMedia rankedMedia = usersMedia.getMedia().toRankedMedia(usersMedia.getPagerankValue());
                    graph.addVertex(rankedMedia);
                }
        );
        if (mergeAnilist) {
            List<Media> anilistItems = anilistService.retrieveCompletedMedia(username);
            // todo check if new items are in anilistItems, which weren't already in the db
            // if so add them:
            anilistItems.forEach(
                    media -> {
                        RankedMedia rankedMedia = media.toRankedMedia();
                        graph.addVertex(rankedMedia);
                    }
            );
            // todo remember to save the new items to the db, refactor this
        }
        // todo if there are new items, reset pagerankvalue for all items to 1/n

    }

    public void savePageRankValues(String username) {
        Graph<RankedMedia, DefaultEdge> graph = usersGraph.get(username);
        for (RankedMedia media : graph.vertexSet()) {
            usersMediaRepository.save(new UsersMedia(new UsersMediaId(username, media.getId()), userRepository.findById(username), media, media.getPageRankValue()));
        }
    }
}
