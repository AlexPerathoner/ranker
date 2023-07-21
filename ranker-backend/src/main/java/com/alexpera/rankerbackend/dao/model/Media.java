package com.alexpera.rankerbackend.dao.model;

import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medias")
public class Media {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "meta")
    private String meta;

    public RankedMedia toRankedMedia() {
        return RankedMedia.builder()
                .id(this.getId())
                .meta(this.getMeta())
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
}
