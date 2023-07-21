package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediaTest {
    @Test
    public void equalsTest() {
        Media media1 = Media.builder()
                .id(1L)
                .meta("some meta string here")
                .build();
        Media media2 = Media.builder()
                .id(1L)
                .meta("some other meta string here")
                .build();

        assertEquals(media1, media2);
    }
    @Test
    public void notEqualsTest() {
        Media media1 = Media.builder()
                .id(1L)
                .meta("some meta string here")
                .build();
        Media media2 = Media.builder()
                .id(3L)
                .meta("some meta string here")
                .build();

        assertNotEquals(media1, media2);
    }

    @Test
    public void toRankedMediaTest() {
        Double randomPageRankValue = 0.2;
        Media media = Media.builder()
                .id(1L)
                .meta("some meta string here")
                .build();
        RankedMedia rankedMedia = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .pageRankValue(randomPageRankValue)
                .meta("some meta string here")
                .build();

        assertEquals(rankedMedia, media.toRankedMedia());
    }
}
