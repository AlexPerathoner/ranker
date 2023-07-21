package com.alexpera.rankerbackend.service.pagerank;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.model.User;
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
        // todo redo
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
        if (usersGraph.containsKey(username)) {
            return new HashSet<>();
        }

        // todo load from db
        // load from anilist, too 

        Graph<RankedMedia, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        usersMediaRepository.findAllByUserId(username).forEach(
                usersMedia -> graph.addVertex(new RankedMedia(usersMedia.getMedia(), usersMedia.getPagerankValue()))
        );
        edgeRepository.findByUsername(username).forEach(edge -> {
            graph.addEdge(edge.getBetter().toRankedMedia(), edge.getWorse().toRankedMedia());
        });

        usersGraph.put(username, graph);
        return graph.edgeSet();
    }

    public void savePageRankValues(String username) {
        Graph<RankedMedia, DefaultEdge> graph = usersGraph.get(username);
        for (RankedMedia media : graph.vertexSet()) {
            usersMediaRepository.save(new UsersMedia(new UsersMediaId(username, media.getId()), userRepository.findById(username), media, media.getPageRankValue()));
        }
    }
}
