package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.exceptions.LoopException;
import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import com.alexpera.rankerbackend.model.anilist.VotedMedia;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.alexpera.rankerbackend.CommonMethods.createMedia;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageRankServiceTest {
    PageRankService pageRankService;

    Graph<RankedMedia, DefaultEdge> graph;

    RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(1.0);
    RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(2.0);
    RankedMedia rankedMedia3 = createMedia(3L).toRankedMedia(3.0);

    @BeforeEach
    void setup() {
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        graph.addVertex(rankedMedia1);
        graph.addVertex(rankedMedia2);
        graph.addVertex(rankedMedia3);
    }

    @Test
    void initialValueTest() {
        PageRankService.resetPageRankToInitialValue(graph);
        graph.vertexSet().forEach(v -> assertEquals(1./3, v.getPageRankValue()));
    }

    @Test
    void oneLinkTest() {
        graph.addEdge(rankedMedia1, rankedMedia2);
        PageRankService.calculateIteration(graph);
        assertTrue(Double.compare(rankedMedia1.getPageRankValue(), rankedMedia2.getPageRankValue()) < 0);
    }

    @Test
    void twoLinkTest() {
        graph.addEdge(rankedMedia1, rankedMedia2);
        graph.addEdge(rankedMedia2, rankedMedia3);
        PageRankService.calculateIteration(graph);
        assertTrue(Double.compare(rankedMedia1.getPageRankValue(), rankedMedia2.getPageRankValue()) < 0);
        assertTrue(Double.compare(rankedMedia2.getPageRankValue(), rankedMedia3.getPageRankValue()) < 0);
    }

    @Test
    void loopTest() {
        graph.addEdge(rankedMedia1, rankedMedia2);
        graph.addEdge(rankedMedia2, rankedMedia1);
        PageRankService.calculateIteration(graph);
        assertEquals(rankedMedia1.getPageRankValue(), rankedMedia2.getPageRankValue());
    }

    @Test
    void loopTestWithLeafIngoing() {
        graph.addEdge(rankedMedia1, rankedMedia2);
        graph.addEdge(rankedMedia2, rankedMedia1);
        graph.addEdge(rankedMedia3, rankedMedia1);
        PageRankService.calculateIteration(graph);

        assertTrue(Double.compare(rankedMedia3.getPageRankValue(), rankedMedia1.getPageRankValue()) < 0);
        assertTrue(Double.compare(rankedMedia3.getPageRankValue(), rankedMedia2.getPageRankValue()) < 0);
    }

    @Test
    void loopTestWithLeafOutgoing() {
        graph.addEdge(rankedMedia1, rankedMedia2);
        graph.addEdge(rankedMedia2, rankedMedia1);
        graph.addEdge(rankedMedia1, rankedMedia3);
        PageRankService.calculateIteration(graph);

        assertTrue(Double.compare(rankedMedia3.getPageRankValue(), rankedMedia1.getPageRankValue()) < 0);
        assertTrue(Double.compare(rankedMedia2.getPageRankValue(), rankedMedia1.getPageRankValue()) < 0);
    }

    @Test
    void oneLinkTestMultipleIterations() {
        graph.addEdge(rankedMedia1, rankedMedia2);
        PageRankService.calculateIteration(graph);
        PageRankService.calculateIteration(graph);
        PageRankService.calculateIteration(graph);
        assertTrue(Double.compare(rankedMedia1.getPageRankValue(), rankedMedia2.getPageRankValue()) < 0);
    }

    @Test
    void noDuplicateEdges() {
        graph.addEdge(rankedMedia1, rankedMedia2);
        graph.addEdge(rankedMedia2, rankedMedia3);
        PageRankService.calculateIteration(graph);
        double pv1 = rankedMedia1.getPageRankValue();
        double pv2 = rankedMedia2.getPageRankValue();
        int edgesCount = graph.edgeSet().size();

        graph.addEdge(rankedMedia1, rankedMedia2);
        PageRankService.calculateIteration(graph);
        assertEquals(pv1, rankedMedia1.getPageRankValue()); // checking pagerank value didnt change
        assertEquals(pv2, rankedMedia2.getPageRankValue());

        assertEquals(edgesCount, graph.edgeSet().size()); // checking edges count didnt change
    }
}
