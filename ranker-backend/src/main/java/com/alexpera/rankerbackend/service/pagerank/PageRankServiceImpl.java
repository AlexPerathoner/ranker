package com.alexpera.rankerbackend.service.pagerank;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.Edge;
import com.alexpera.rankerbackend.model.anilist.RankedAnilistMedia;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
// todo add logs for every method
@Component
public class PageRankServiceImpl implements PageRankService<AnilistMedia> {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EdgeRepository edgeRepository;

    private final HashMap<String, Graph<Media, DefaultEdge>> usersGraph = new HashMap<>();

    static final double DAMPING_FACTOR = 0.85;

     private Graph<Media, DefaultEdge> getGraph(String username) {
        if (!usersGraph.containsKey(username)) {
            usersGraph.put(username, new DefaultDirectedGraph<>(DefaultEdge.class));
        }
        return usersGraph.get(username);
    }

    @Override
    public void add(String username, Media item) {
        if (!getGraph(username).containsVertex(item)) {
            getGraph(username).addVertex(item);
        }
    }

    @Override
    public void addAll(String username, ArrayList<Media> items) {
        items.forEach(anilistMedia -> add(username, anilistMedia));
    }

    @Override
    public void addLink(String username, Media better, Media worse) {
        // todo check for cycles, remove them
        getGraph(username).addEdge(worse, better);
    }

    @Override
    public void calculateIteration(String username) {

        HashMap<AnilistMedia, Double> newValues = new HashMap<>();
        for (Media item : getGraph(username).vertexSet()) {
            double sum = 0;
            for (DefaultEdge edge : getGraph(username).incomingEdgesOf(item)) {
                Media other = getGraph(username).getEdgeSource(edge);
                sum += other.getPageRankValue() / getGraph(username).outDegreeOf(other);
            }
            newValues.put(item, (1 - DAMPING_FACTOR) / getGraph(username).vertexSet().size() + DAMPING_FACTOR * sum);
        }

        for (Media item : getGraph(username).vertexSet()) {
            item.setPageRankValue(newValues.get(item));
        }
    }

    @Override
    public Set<AnilistMedia> getItems(String username) {
        return getGraph(username).vertexSet();
    }

    @Override
    public List<AnilistMedia> getItemsSorted(String username) {
        return getGraph(username).vertexSet().stream().sorted().toList();
    }

    @Override
    public List<AnilistMedia> getItemsRanked(String username, DistributionFunction distribution) {
        List<AnilistMedia> items = getItemsSorted(username);
        List<RankedAnilistMedia> rankedItems = new ArrayList<>();
        for (AnilistMedia item : items) {
            double rank;
            if (distribution == DistributionFunction.constant) {
                rank = (items.size() - items.indexOf(item)) / (double) items.size();
                rankedItems.add(new RankedAnilistMedia(item, rank));
            }
        }
        return rankedItems.stream().sorted((a, b) -> Double.compare(b.getRank(), a.getRank())).map(RankedAnilistMedia::getAnilistMedia).toList();
    }

    @Override
    public Set<AnilistMedia> getNextComparison(String username) throws RuntimeException {
        // find item with lowest number of incoming or outgoing edges
        AnilistMedia minItem = null;
        int minEdges = Integer.MAX_VALUE;
        for (AnilistMedia item : getGraph(username).vertexSet()) {
            int edges = getGraph(username).inDegreeOf(item) + getGraph(username).outDegreeOf(item);
            if (edges < minEdges) {
                minEdges = edges;
                minItem = item;
            }
        }

        // get mid item
        List<AnilistMedia> items = getItemsSorted(username);
        AnilistMedia midItem = items.get(items.size() / 2);

        if (minItem == null) {
            throw new RuntimeException("No items in graph");
        }
        return Set.of(minItem, midItem);
    }

    @Override
    public Set<Edge<AnilistMedia>> getEdges(String username) {
        Set<DefaultEdge> edges = getGraph(username).edgeSet();
        HashSet<Edge<AnilistMedia>> result = new HashSet<>();
        for (DefaultEdge edge : edges) {
            result.add(new Edge<>(getGraph(username).getEdgeSource(edge), getGraph(username).getEdgeTarget(edge)));
        }
        return result;
    }

    @Override
    public void loadUser(String username) {
        if (usersGraph.containsKey(username)) {
         return;
        }
        // todo load from db
        // load from anilist, too 

        Graph<Media, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        userRepository.findById(username).getMedias().forEach(graph::addVertex);
        edgeRepository.findByUsername(username).forEach(edge -> graph.addEdge(edge.getBetter(), edge.getWorse()));
        usersGraph.put(username, graph);
    }


}
