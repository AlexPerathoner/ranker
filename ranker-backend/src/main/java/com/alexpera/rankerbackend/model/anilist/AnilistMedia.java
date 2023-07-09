package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.service.pagerank.Identifiable;
import lombok.Data;

import java.util.UUID;

@Data
public class AnilistMedia implements Identifiable {
    private long mediaId;

    @Override
    public long getId() {
        return mediaId;
    }

    @Override
    public void setId(long id) {
        mediaId = id;
    }

    @Data
    private static class Media {
        @Data
        private static class Title {
            private String romaji;
            private String english;
            private String userPreferred;
        }
        @Data
        private static class CoverImage {
            private String extraLarge;
            private String large;
            private String medium;
            private String color;
        }
        private Title title;
        private CoverImage coverImage;
    }
    private Media media;
}
