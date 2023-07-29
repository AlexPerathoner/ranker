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
public class PageRankService {

    static final double DAMPING_FACTOR = 0.85;

    public static void calculateIteration(Graph<RankedMedia, DefaultEdge> userGraph) {
        HashMap<RankedMedia, Double> newValues = new HashMap<>();
        for (RankedMedia item : userGraph.vertexSet()) {
            newValues.put(item, calculatePageRankOfMedia(userGraph, newValues, item));
        }

        for (RankedMedia item : userGraph.vertexSet()) {
            item.setPageRankValue(newValues.get(item));
        }
    }

    private static double calculatePageRankOfMedia(Graph<RankedMedia, DefaultEdge> userGraph, HashMap<RankedMedia, Double> pageRankValues, RankedMedia vertex) {
        AtomicReference<Double> accumulator = new AtomicReference<>(0.0);
        userGraph.incomingEdgesOf(vertex).forEach(edge -> {
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
