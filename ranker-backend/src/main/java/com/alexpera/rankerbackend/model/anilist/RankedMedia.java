package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class RankedMedia extends Media implements Comparable<Media> {
    private Double pageRankValue;

    @Override
    public int compareTo(Media o) {
        return Double.compare(this.getPageRankValue(), ((RankedMedia) o).getPageRankValue());
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof RankedMedia item) {
            return this.getId() == item.getId();
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pageRankValue);
    }

    @Builder
    public RankedMedia(Long id, String meta, Double pageRankValue) {
        super(id, meta);
        this.pageRankValue = pageRankValue;
    }

    public RankedMedia(Media media, Double pageRankValue) {
        super(media.getId(), media.getMeta());
        this.pageRankValue = pageRankValue;
    }

    public RankedMedia(Media media) {
        super(media.getId(), media.getMeta());
        this.pageRankValue = null;
    }
}
