package com.alexpera.rankerbackend.dao.model;

import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "medias")
@SuperBuilder
public class Media {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "meta")
    private String meta;

    public RankedMedia toRankedMedia() {
        return RankedMedia.rankedMediaBuilder()
                .id(this.getId())
                .meta(this.getMeta())
                .build();
    }

    public RankedMedia toRankedMedia(Double pageRankValue) {
        return RankedMedia.rankedMediaBuilder()
                .id(this.getId())
                .meta(this.getMeta())
                .pageRankValue(pageRankValue)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Media item) {
            return Objects.equals(this.getId(), item.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, meta);
    }

    @Builder
    public Media(Long id, String meta) {
        this.id = id;
        this.meta = meta;
    }
}
