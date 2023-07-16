package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.service.pagerank.Identifiable;
import com.alexpera.rankerbackend.service.pagerank.PageRanked;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AnilistMedia implements Identifiable, Comparable<AnilistMedia>, PageRanked {
    private long mediaId;
    private double pagerankValue;

    @Override
    public long getId() {
        return mediaId;
    }

    @Override
    public void setId(long id) {
        mediaId = id;
    }

    @Override
    public int compareTo(AnilistMedia o) {
        return Double.compare(pagerankValue, o.pagerankValue);
    }

    @Override
    public double getPageRankValue() {
        return pagerankValue;
    }

    @Override
    public void setPageRankValue(double value) {
        pagerankValue = value;
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
