package com.alexpera.rankerbackend.service.pagerank;

import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface PageRankService<T extends Identifiable> {
    void add(T item);
    void addAll(ArrayList<T> items);

    void addLink(T better, T worse);

    void calculateIteration();

//    void importFrom(PageRankService<T> other);

    Set<T> getItems();
    List<T> getItemsSorted();
    List<T> getItemsRanked(DistributionFunction distribution);

    Set<T> getNextComparison();

    Set<Edge<T>> getEdges();

}
