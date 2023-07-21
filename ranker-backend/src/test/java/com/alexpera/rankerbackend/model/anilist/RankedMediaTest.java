package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RankedMediaTest {
    @Test
    public void equalsTest() {

        RankedMedia rankedMedia = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .build();
        RankedMedia rankedMedia2 = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some other meta string here")
                .pageRankValue(0.5)
                .build();
        
        assertEquals(rankedMedia, rankedMedia2);
    }

    @Test
    public void compareTestNull() {
        RankedMedia rankedMedia = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .build();
        RankedMedia rankedMedia2 = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some other meta string here")
                .pageRankValue(0.5)
                .build();

        assertThrows(NullPointerException.class, () -> {
            rankedMedia.compareTo(rankedMedia2);
        });
    }
    @Test
    public void compareTestSmaller() {
        RankedMedia rankedMedia = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .pageRankValue(0.1)
                .build();
        RankedMedia rankedMedia2 = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some other meta string here")
                .pageRankValue(0.5)
                .build();

        assertEquals(-1, rankedMedia.compareTo(rankedMedia2));
    }
    @Test
    public void compareTestBigger() {
        RankedMedia rankedMedia = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .pageRankValue(0.1)
                .build();
        RankedMedia rankedMedia2 = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some other meta string here")
                .pageRankValue(0.5)
                .build();

        assertEquals(1, rankedMedia2.compareTo(rankedMedia));
    }
    @Test
    public void compareTestEqual() {
        RankedMedia rankedMedia = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .pageRankValue(0.5)
                .build();
        RankedMedia rankedMedia2 = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some other meta string here")
                .pageRankValue(0.5)
                .build();

        assertEquals(0, rankedMedia2.compareTo(rankedMedia));
    }
    @Test
    public void compareTestMedia() {
        Media rankedMedia = Media.builder()
                .id(1L)
                .meta("some meta string here")
                .build();
        RankedMedia rankedMedia2 = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some other meta string here")
                .pageRankValue(0.5)
                .build();
        assertEquals(rankedMedia2, rankedMedia);
    }
}
