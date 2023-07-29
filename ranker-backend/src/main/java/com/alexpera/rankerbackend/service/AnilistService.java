package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnilistService {

    // todo: suzu implementa sta roba
    // todo: graphql request to anilist
    public List<Media> retrieveCompletedMedia(String username) {
        return new ArrayList<>();
    }
}
