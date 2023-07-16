package com.alexpera.rankerbackend.dao;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PageRankDAOImpl implements PageRankDAO {
    private final HashMap<String, Graph<AnilistMedia, DefaultEdge>> graph = new HashMap<>();

    @Override
    public Graph<AnilistMedia, DefaultEdge> getGraph(String username) {
        if (!graph.containsKey(username)) {
            graph.put(username, new DefaultDirectedGraph<>(DefaultEdge.class));
        }
        return graph.get(username);
    }
}
