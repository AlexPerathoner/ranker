package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import com.alexpera.rankerbackend.model.anilist.VotedMedia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {
    VoteService voteService;

    @Mock
    PageRankService pageRankService;

    @BeforeEach
    void setUp() {
        voteService = new VoteService(pageRankService);
    }

    private final static String USER = "Piede";

    private Media createMedia(Long id) {
        return Media.builder()
                .id(id)
                .build();
    }

    @Test
    void getItemsVoted() {
        RankedMedia rankedMedia1 = createMedia(1L).toRankedMedia(0.4);
        RankedMedia rankedMedia2 = createMedia(2L).toRankedMedia(0.5);
        RankedMedia rankedMedia3 = createMedia(3L).toRankedMedia(1.0);
        when(pageRankService.getItemsSorted(USER)).thenReturn(List.of(rankedMedia1, rankedMedia2, rankedMedia3));
        List<VotedMedia> result = voteService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 0, 10);
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
        when(pageRankService.getItemsSorted(USER)).thenReturn(List.of(rankedMedia1, rankedMedia2, rankedMedia3));

        List<VotedMedia> result = voteService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 5, 10);

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
        when(pageRankService.getItemsSorted(USER)).thenReturn(List.of(rankedMedia1, rankedMedia2, rankedMedia3));
        List<VotedMedia> result = voteService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 0, 6);

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
        when(pageRankService.getItemsSorted(USER)).thenReturn(List.of(rankedMedia1, rankedMedia2, rankedMedia3));
        List<VotedMedia> result = voteService.getItemsVotedAsc(USER, DistributionFunction.LINEAR, 2, 6);

        // check values
        assertEquals(2, result.get(0).getVote());
        assertEquals(4, result.get(1).getVote());
        assertEquals(6, result.get(2).getVote());
    }
}
