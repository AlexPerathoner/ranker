package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {
    private final Double PAGERANK_VALUE = 0.2;
    private Media createMedia(Long id) {
        return Media.builder()
                .id(id)
                .meta("some meta string here")
                .build();
    }
    @Test
    void equalsTest() {
        Media media1 = createMedia(1L);
        Media media2 = createMedia(1L);

        assertEquals(media1, media2);
    }
    @Test
    void notEqualsTest() {
        Media media1 = createMedia(1L);
        Media media2 = createMedia(3L);

        assertNotEquals(media1, media2);
    }

    @Test
    void toRankedMediaTest() {
        Media media = createMedia(1L);
        RankedMedia rankedMedia = media.toRankedMedia(PAGERANK_VALUE);

        assertEquals(rankedMedia, media.toRankedMedia());
        assertEquals(rankedMedia.getId(), media.getId());
        assertEquals(rankedMedia.getMeta(), media.getMeta());
        assertEquals(PAGERANK_VALUE, rankedMedia.getPageRankValue());
    }
    @Test
    void toRankedMediaTestKeepValues() {
        RankedMedia media = RankedMedia.rankedMediaBuilder()
                .id(1L)
                .meta("some meta string here")
                .pageRankValue(PAGERANK_VALUE)
                .build();
        RankedMedia rankedMedia = media.toRankedMedia();

        assertEquals(rankedMedia, media.toRankedMedia());
        assertEquals(rankedMedia.getId(), media.getId());
        assertEquals(rankedMedia.getMeta(), media.getMeta());
        assertEquals(rankedMedia.getPageRankValue(), media.getPageRankValue());
    }
}
