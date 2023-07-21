package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnilistServiceTest {
    @Autowired
    AnilistService anilistService;

    @Test
    public void retrieveCompletedMediaTest() {
        List<Media> mediaList = anilistService.retrieveCompletedMedia("Piede");
        assertTrue(mediaList.contains(Media.builder()
                .id(416L) // Porco Rosso
                .build()));
    }
}
