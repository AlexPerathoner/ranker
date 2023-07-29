package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.exceptions.LoopException;
import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import com.alexpera.rankerbackend.model.anilist.VotedMedia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        pageRankService = new PageRankService();
    }

    private final static String USER = "Piede";
    private final static Media MEDIA = Media.builder()
            .id(416L)
            .build();
    private Media createMedia(Long id) {
        return Media.builder()
                .id(id)
                .build();
    }



    // todo test with ~10 items and normal distribution
}
