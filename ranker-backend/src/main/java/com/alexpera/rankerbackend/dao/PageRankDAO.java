package com.alexpera.rankerbackend.dao;

import com.alexpera.rankerbackend.model.PageRank;

public interface PageRankDAO {
    void savePageRankForUser(PageRank pageRank, String userId);

    PageRank getPageRankForUser(String userId);

}
