package com.alexpera.rankerbackend.service.pagerank;

import java.util.ArrayList;
import java.util.Set;

public interface PageRankService<T extends Identifiable> {
    void add(T item);
    void addAll(ArrayList<T> items);

    void addLink(T better, T worse);

    void calculateIteration();

    void importFrom(PageRankService<T> other);

    Iterable<T> getItems();
    Iterable<T> getItemsSorted();
    Iterable<T> getItemsRanked(DistributionFunction distribution);

    Set<T> getNextComparison();

}
