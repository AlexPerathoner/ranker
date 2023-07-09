package com.alexpera.rankerbackend.model;

import com.alexpera.rankerbackend.service.pagerank.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
public class PageRankItem<T extends Identifiable> {
    private T item;
    private Double rank;

//    private Collection<PageRankItem<T>> better;
//    private Collection<PageRankItem<T>> worse;

    public PageRankItem(T item) {
        this.item = item;
        this.rank = 1.0;
    }
}
