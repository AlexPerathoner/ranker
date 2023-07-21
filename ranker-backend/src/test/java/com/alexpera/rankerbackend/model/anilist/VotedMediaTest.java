package com.alexpera.rankerbackend.model.anilist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class VotedMediaTest {
    @Test
    public void equalsTest() {
        VotedMedia votedMedia1 = VotedMedia.votedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .vote(10.0)
                .build();
        VotedMedia votedMedia2 = VotedMedia.votedMediaBuilder()
                .id(1L)
                .meta("some other meta string here")
                .vote(4.5)
                .build();

        assertEquals(votedMedia1, votedMedia2);
    }
    @Test
    public void notEqualsTest() {
        VotedMedia votedMedia1 = VotedMedia.votedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .vote(10.0)
                .build();
        VotedMedia votedMedia2 = VotedMedia.votedMediaBuilder()
                .id(2L)
                .meta("some other meta string here")
                .vote(4.5)
                .build();

        assertNotEquals(votedMedia1, votedMedia2);
    }

}
