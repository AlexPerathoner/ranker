package com.alexpera.rankerbackend.dao;


import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface PageRankDAO {
    Graph<AnilistMedia, DefaultEdge> getGraph(String username);
//    void savePageRankForUser(Graph pageRank, String userId);
//
//    PageRank getPageRankForUser(String userId);

}
