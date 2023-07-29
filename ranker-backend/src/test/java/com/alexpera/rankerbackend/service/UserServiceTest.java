package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static com.alexpera.rankerbackend.CommonMethods.createMedia;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    UserService userService;
    @Mock
    EdgeRepository edgeRepository;
    @Mock
    MediaRepository mediaRepository;
    @Mock
    UsersMediaRepository usersMediaRepository;
    @Mock
    AnilistService anilistService;

    @BeforeEach
    void setup() {
        userService = new UserService(edgeRepository, mediaRepository, usersMediaRepository, anilistService);
    }

    private final static String USER = "Piede";
    private final static Media MEDIA = Media.builder()
            .id(416L)
            .build();

    @Test
    void loadUserTest() {
        ArrayList<Media> mediaList = new ArrayList<>();
        mediaList.add(MEDIA);
        when(anilistService.retrieveCompletedMedia(USER)).thenReturn(mediaList);
        userService.loadUser(USER);
        assertTrue(userService.getItems(USER).contains(MEDIA.toRankedMedia()));
    }

    @Test
    void addVertexTest() {
        RankedMedia rankedMedia = MEDIA.toRankedMedia();
        userService.add(USER, rankedMedia);
        assertTrue(userService.getItems(USER).contains(rankedMedia));
    }

    @Test
    void addAllVertexTest() {
        ArrayList<RankedMedia> rankedMediaList = new ArrayList<>();
        rankedMediaList.add(createMedia(1L).toRankedMedia());
        rankedMediaList.add(createMedia(1L).toRankedMedia());
        userService.addAll(USER, rankedMediaList);
        assertEquals(1, userService.getItems(USER).size());

        rankedMediaList.add(createMedia(2L).toRankedMedia());
        userService.addAll(USER, rankedMediaList);
        assertEquals(2, userService.getItems(USER).size());

        assertTrue(userService.getItems(USER).containsAll(rankedMediaList));
    }

}
