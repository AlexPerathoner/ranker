package com.alexpera.rankerbackend.model;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.service.pagerank.DistributionFunction;
import com.alexpera.rankerbackend.service.pagerank.Identifiable;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

@Data
public class PageRank<T extends Identifiable> {
//    private String userId;
//    private DistributionFunction preferredDistributionFunction;

    private Collection<PageRankItem<T>> items;
    private Collection<PageRankLink<T>> links;

    public PageRank() {
        items = new HashSet<>();
        links = new HashSet<>();
    }
}
