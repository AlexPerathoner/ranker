package com.alexpera.rankerbackend.service.pagerank;

import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.Edge;
import com.alexpera.rankerbackend.model.anilist.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// todo remove interface, use only implementation, rename classes
// todo add tests?
public interface PageRankService<T extends Identifiable> {
    void add(String username, T item);
    void addAll(String username, ArrayList<T> items);

    void addLink(String username, T better, T worse);

    void calculateIteration(String username);

//    void importFrom(PageRankService<T> other);

    Set<T> getItems(String username);
    List<T> getItemsSorted(String username);
    List<T> getItemsRanked(String username, DistributionFunction distribution);

    Set<T> getNextComparison(String username);

    Set<Edge<T>> getEdges(String username);

    void loadUser(String username);

}
