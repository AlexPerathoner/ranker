package com.alexpera.rankerbackend.model.anilist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RankedMediaTest {
    @Test
    public void equalsTest() {
        RankedMedia rankedMedia = RankedMedia.builder()
                .id(1L)
                .meta("some meta string here")
                .build();
        RankedMedia rankedMedia2 = RankedMedia.builder()
                .id(1L)
                .meta("some other meta string here")
                .pageRankValue(0.5)
                .build();
        assertEquals(rankedMedia, rankedMedia2);
    }
}
