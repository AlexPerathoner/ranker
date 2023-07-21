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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageRankServiceTest {
    PageRankService pageRankService;

    @Mock
    UserRepository userRepository;
    @Mock
    UsersMediaRepository usersMediaRepository;
    @Mock
    EdgeRepository edgeRepository;
    @Mock
    MediaRepository mediaRepository;
    @Mock
    AnilistService anilistService;

    @BeforeEach
    void setUp() {
        pageRankService = new PageRankService(userRepository, usersMediaRepository, edgeRepository, mediaRepository, anilistService);
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

    @Test
    void loadUserTest() {
        ArrayList<Media> mediaList = new ArrayList<>();
        mediaList.add(MEDIA);
        when(anilistService.retrieveCompletedMedia(USER)).thenReturn(mediaList);
        pageRankService.loadUser(USER);
        assertTrue(pageRankService.getItems(USER).contains(MEDIA.toRankedMedia()));
    }

    @Test
    void addVertexTest() {
        RankedMedia rankedMedia = MEDIA.toRankedMedia();
        pageRankService.add(USER, rankedMedia);
        assertTrue(pageRankService.getItems(USER).contains(rankedMedia));
        assertTrue(pageRankService.getItemsSorted(USER).contains(rankedMedia));
    }

    @Test
    void addAllVertexTest() {
        ArrayList<RankedMedia> rankedMediaList = new ArrayList<>();
        rankedMediaList.add(createMedia(1L).toRankedMedia());
        rankedMediaList.add(createMedia(1L).toRankedMedia());
        pageRankService.addAll(USER, rankedMediaList);
        assertEquals(1, pageRankService.getItems(USER).size());

        rankedMediaList.add(createMedia(2L).toRankedMedia());
        pageRankService.addAll(USER, rankedMediaList);
        assertEquals(2, pageRankService.getItems(USER).size());

        assertTrue(pageRankService.getItems(USER).containsAll(rankedMediaList));
    }
    @Test
    void getItemsSorted() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(1.0);
        RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(2.0);
        pageRankService.add(USER, rankedMedia1);
        pageRankService.add(USER, rankedMedia2);
        assertEquals(2, pageRankService.getItems(USER).size());

        assertEquals(pageRankService.getItemsSorted(USER).get(0), rankedMedia1);
        assertEquals(pageRankService.getItemsSorted(USER).get(1), rankedMedia2);
    }

    @Test
    void addEdgeTest() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia();
        RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia();
        pageRankService.add(USER, rankedMedia1);
        pageRankService.add(USER, rankedMedia2);
        pageRankService.addLink(USER, rankedMedia1, rankedMedia2);
        assertEquals(pageRankService.getEdges(USER).stream().toList().get(0).getSource(), rankedMedia1);
        assertEquals(pageRankService.getEdges(USER).stream().toList().get(0).getTarget(), rankedMedia2);
    }

    @Test
    void addLinkOnSameVertex() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia();
        pageRankService.add(USER, rankedMedia1);
        
        assertThrows(LoopException.class, () -> pageRankService.addLink(USER, rankedMedia1, rankedMedia1));
    }

    @Test
    void getItemsVoted() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(0.4);
        RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(0.5);
        RankedMedia rankedMedia3 = createMedia(3L).toRankedMedia(1.0);
        pageRankService.add(USER, rankedMedia1);
        pageRankService.add(USER, rankedMedia2);
        pageRankService.add(USER, rankedMedia3);
        List<VotedMedia> result = pageRankService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 0, 10);
        assertEquals(3, result.size());
        // check order
        assertEquals(result.get(0), rankedMedia1);
        assertEquals(result.get(1), rankedMedia2);
        assertEquals(result.get(2), rankedMedia3);

        // check values
        assertEquals(0.0, result.get(0).getVote());
        assertEquals(5.0, result.get(1).getVote());
        assertEquals(10.0, result.get(2).getVote());
    }
    @Test
    void getItemsVotedCustomMin() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(0.4);
        RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(0.5);
        RankedMedia rankedMedia3 = createMedia(3L).toRankedMedia(1.0);
        pageRankService.add(USER, rankedMedia1);
        pageRankService.add(USER, rankedMedia2);
        pageRankService.add(USER, rankedMedia3);
        List<VotedMedia> result = pageRankService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 5, 10);

        // check values
        assertEquals(5.0, result.get(0).getVote());
        assertEquals(7.5, result.get(1).getVote());
        assertEquals(10.0, result.get(2).getVote());
    }
    @Test
    void getItemsVotedCustomMax() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(0.4);
        RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(0.5);
        RankedMedia rankedMedia3 = createMedia(3L).toRankedMedia(1.0);
        pageRankService.add(USER, rankedMedia1);
        pageRankService.add(USER, rankedMedia2);
        pageRankService.add(USER, rankedMedia3);
        List<VotedMedia> result = pageRankService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 0, 6);

        // check values
        assertEquals(0, result.get(0).getVote());
        assertEquals(3, result.get(1).getVote());
        assertEquals(6, result.get(2).getVote());
    }
    @Test
    void getItemsVotedCustomMinMax() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(0.4);
        RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(0.5);
        RankedMedia rankedMedia3 = createMedia(3L).toRankedMedia(1.0);
        pageRankService.add(USER, rankedMedia1);
        pageRankService.add(USER, rankedMedia2);
        pageRankService.add(USER, rankedMedia3);
        List<VotedMedia> result = pageRankService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 2, 6);

        // check values
        assertEquals(2, result.get(0).getVote());
        assertEquals(4, result.get(1).getVote());
        assertEquals(6, result.get(2).getVote());
    }

    // todo test with ~10 items and normal distribution
}
