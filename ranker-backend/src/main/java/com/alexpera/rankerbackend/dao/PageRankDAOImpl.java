package com.alexpera.rankerbackend.dao;

import com.alexpera.rankerbackend.config.MongoDBUtil;
import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.model.anilist.Edge;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class PageRankDAOImpl implements PageRankDAO {

    @Getter
    MongoCollection<Edge> edges = MongoDBUtil.getEdges();

    public List<T> getAll() {
        return collection.find().into(new ArrayList<>());
    }
    private final HashMap<String, Graph<AnilistMedia, DefaultEdge>> graph = new HashMap<>();

    @Override
    public Graph<AnilistMedia, DefaultEdge> getGraph(String username) {
        if (!graph.containsKey(username)) {
            graph.put(username, new DefaultDirectedGraph<>(DefaultEdge.class));
        }
        return graph.get(username);
    }
}
