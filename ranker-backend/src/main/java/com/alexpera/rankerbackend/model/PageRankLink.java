package com.alexpera.rankerbackend.model;

import com.alexpera.rankerbackend.service.pagerank.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageRankLink<T extends Identifiable> {
    PageRankItem<T> better;
    PageRankItem<T> worse;
}
