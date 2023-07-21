package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void test() {
        ArrayList<Media> mediaList = new ArrayList<>();
        mediaList.add(MEDIA);
        when(anilistService.retrieveCompletedMedia(USER)).thenReturn(mediaList);
        pageRankService.loadUser(USER, true);
        assertTrue(pageRankService.getItems(USER).contains(MEDIA.toRankedMedia()));
    }
}
