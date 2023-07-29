package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.exceptions.LoopException;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RankerServiceTest {
    RankerService rankerService;

    @Mock
    UserRepository userRepository;
    @Mock
    UsersMediaRepository usersMediaRepository;
    @Mock
    MediaRepository mediaRepository;
    @Mock
    UserService userService;

    Graph<RankedMedia, DefaultEdge> graph;

    @BeforeEach
    void setup() {
        rankerService = new RankerService(userRepository, usersMediaRepository, mediaRepository, userService);
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        graph.addVertex(rankedMedia1);
        graph.addVertex(rankedMedia2);
    }

    private final static String USER = "Piede";
    private final static Media MEDIA = Media.builder()
            .id(416L)
            .build();
    private Media createMedia(Long id) {
        return Media.builder()
                .id(id)
                .build();
    }
    RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(1.0);
    RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(2.0);

    @Test
    void getItemsSorted() {
        when(userService.getItems(USER)).thenReturn(graph.vertexSet());
        assertEquals(rankerService.getItemsSorted(USER).get(0), rankedMedia1);
        assertEquals(rankerService.getItemsSorted(USER).get(1), rankedMedia2);
    }

    @Test
    void addEdgeTest() {
        when(userService.getGraph(USER)).thenReturn(graph);
        rankerService.addLink(USER, rankedMedia1, rankedMedia2);
        assertEquals(rankerService.getEdges(USER).stream().toList().get(0).getSource(), rankedMedia1);
        assertEquals(rankerService.getEdges(USER).stream().toList().get(0).getTarget(), rankedMedia2);
    }

    @Test
    void addLinkOnSameVertex() {
        assertThrows(LoopException.class, () -> rankerService.addLink(USER, rankedMedia1, rankedMedia1));
    }
}
