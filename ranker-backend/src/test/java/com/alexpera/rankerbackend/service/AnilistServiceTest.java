package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnilistServiceTest {
    AnilistService anilistService = new AnilistService();

    @Test
    @Disabled("Not yet implemented.")
    public void retrieveCompletedMediaTest() {
        List<Media> mediaList = anilistService.retrieveCompletedMedia("Piede");
        assertTrue(mediaList.contains(Media.builder()
                .id(416L) // Porco Rosso
                .build()));
    }
}
