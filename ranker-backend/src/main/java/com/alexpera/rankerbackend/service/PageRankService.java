package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

// todo add logs for every method
public class PageRankService {

    static final double DAMPING_FACTOR = 0.85;

    public static void calculateIteration(Graph<RankedMedia, DefaultEdge> userGraph) {
        HashMap<RankedMedia, Double> newValues = initPageRankValues(userGraph);

        for (RankedMedia item : userGraph.vertexSet()) {
            newValues.put(item, calculatePageRankOfMedia(userGraph, newValues, item));
        }

        userGraph.vertexSet().forEach(v -> {
            v.setPageRankValue(newValues.get(v));
        });
    }

    private static HashMap<RankedMedia, Double> initPageRankValues(Graph<RankedMedia, DefaultEdge> userGraph) {
        HashMap<RankedMedia, Double> values = new HashMap<>();
        double pageRankValue = 1./userGraph.vertexSet().size();
        userGraph.vertexSet().forEach(v -> values.put(v, pageRankValue));
        return values;
    }

    private static double calculatePageRankOfMedia(Graph<RankedMedia, DefaultEdge> userGraph, HashMap<RankedMedia, Double> pageRankValues, RankedMedia vertex) {
        AtomicReference<Double> accumulator = new AtomicReference<>(0.0);
        userGraph.incomingEdgesOf(vertex).forEach(edge -> { // todo check why throws exception
            RankedMedia node = userGraph.getEdgeSource(edge);
            accumulator.set(accumulator.get() + pageRankValues.get(node) / userGraph.outgoingEdgesOf(node).size());
        });
        return (1 - DAMPING_FACTOR) / userGraph.vertexSet().size() + accumulator.get() * DAMPING_FACTOR;
    }

    public static void resetPageRankToInitialValue(Graph<RankedMedia, DefaultEdge> graph) {
        double countVertex = graph.vertexSet().size();
        graph.vertexSet().forEach(v -> v.setPageRankValue(1/countVertex));
    }

}
