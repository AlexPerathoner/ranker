package com.alexpera.rankerbackend.service.pagerank;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
public class PageRankServiceImpl implements PageRankService<AnilistMedia> {
    static final double DAMPING_FACTOR = 0.85;

    Graph<AnilistMedia, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

    @Override
    public void add(AnilistMedia item) {
        graph.addVertex(item);
    }

    @Override
    public void addAll(ArrayList<AnilistMedia> items) {
        items.forEach(this::add);
    }

    @Override
    public void addLink(AnilistMedia better, AnilistMedia worse) {
        // todo check for cycles, remove them
        graph.addEdge(worse, better);
    }

    @Override
    public void calculateIteration() {

        HashMap<AnilistMedia, Double> newValues = new HashMap<>();
        for (AnilistMedia item : graph.vertexSet()) {
            double sum = 0;
            for (DefaultEdge edge : graph.incomingEdgesOf(item)) {
                AnilistMedia other = graph.getEdgeSource(edge);
                sum += other.getPageRankValue() / graph.outDegreeOf(other);
            }
            newValues.put(item, (1 - DAMPING_FACTOR) + DAMPING_FACTOR * sum);
        }

        for (AnilistMedia item : graph.vertexSet()) {
            item.setPageRankValue(newValues.get(item));
        }
    }

    @Override
    public Set<AnilistMedia> getItems() {
        return graph.vertexSet();
    }

    @Override
    public List<AnilistMedia> getItemsSorted() {
        return graph.vertexSet().stream().sorted().toList();
    }

    @Override
    public List<AnilistMedia> getItemsRanked(DistributionFunction distribution) {
        List<AnilistMedia> items = getItemsSorted();
        @AllArgsConstructor
        @Data
        class RankedAnilistMedia {
            AnilistMedia anilistMedia;
            double rank;
        }
        List<RankedAnilistMedia> rankedItems = new ArrayList<>();
        for (AnilistMedia item : items) {
            double rank;
            if (distribution == DistributionFunction.constant) {
                rank = (items.size() - items.indexOf(item)) / (double) items.size();
                rankedItems.add(new RankedAnilistMedia(item, rank));
            }
        }
        return rankedItems.stream().sorted((a, b) -> Double.compare(b.rank, a.rank)).map(RankedAnilistMedia::getAnilistMedia).toList();
    }

    @Override
    public Set<AnilistMedia> getNextComparison() {
        // find item with lowest number of incoming or outgoing edges
        AnilistMedia minItem = null;
        int minEdges = Integer.MAX_VALUE;
        for (AnilistMedia item : graph.vertexSet()) {
            int edges = graph.inDegreeOf(item) + graph.outDegreeOf(item);
            if (edges < minEdges) {
                minEdges = edges;
                minItem = item;
            }
        }

        // get mid item
        List<AnilistMedia> items = getItemsSorted();
        AnilistMedia midItem = items.get(items.size() / 2);

        // todo check error
        return Set.of(minItem, midItem);
    }


}
