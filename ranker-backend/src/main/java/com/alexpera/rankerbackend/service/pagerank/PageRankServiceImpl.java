package com.alexpera.rankerbackend.service.pagerank;

import com.alexpera.rankerbackend.model.PageRank;
import com.alexpera.rankerbackend.model.PageRankItem;
import com.alexpera.rankerbackend.model.PageRankLink;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class PageRankServiceImpl<T extends Identifiable> implements PageRankService<T> {
    static final double DAMPING_FACTOR = 0.85;

    PageRank<T> pageRank = new PageRank<>();

    @Override
    public void add(T item) {
        pageRank.getItems().add(new PageRankItem<>(item));
    }

    @Override
    public void addAll(Iterable<T> items) {
        items.forEach(this::add);
    }

    @Override
    public void addLink(T better, T worse) {
        // todo prevent loops
        pageRank.getLinks().add(new PageRankLink<>(new PageRankItem<>(better), new PageRankItem<>(worse)));
    }

    private Collection<PageRankItem<T>> getNodesWorseThan(PageRankItem<T> item) {
        return pageRank.getLinks().stream()
                .filter(link -> link.getBetter().equals(item))
                .map(PageRankLink::getWorse)
                .collect(Collectors.toList());
    }

    private long getNumberOfLinksFromNode(PageRankItem<T> item) {
        return pageRank.getLinks().stream()
                .filter(link -> link.getBetter().equals(item))
                .count();
    }

    private double getPageRankOfNode(PageRankItem<T> item) {
        AtomicReference<Double> accumulator = new AtomicReference<>((double) 0);

        getNodesWorseThan(item).forEach(link -> {
            accumulator.set(accumulator.get() + link.getRank() / getNumberOfLinksFromNode(link));
        });

        return (1 - DAMPING_FACTOR) / pageRank.getItems().size() + accumulator.get() * DAMPING_FACTOR;
    }

    @Override
    public void calculateIteration() {
        pageRank.getItems().forEach(item -> {
            double pageRankValue = getPageRankOfNode(item);
            item.setRank(pageRankValue);
        });
    }

    @Override
    public void importFrom(PageRankService<T> other) {

    }

    @Override
    public Iterable<T> getItems() {
        return null;
    }

    @Override
    public Iterable<T> getItemsSorted() {
        return null;
    }

    @Override
    public Iterable<T> getItemsRanked(DistributionFunction distribution) {
        return null;
    }
}
