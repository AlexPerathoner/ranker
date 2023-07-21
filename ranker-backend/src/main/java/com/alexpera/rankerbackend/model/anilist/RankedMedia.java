package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Data
public class RankedMedia extends Media implements Comparable<Media> {
    private Double pageRankValue;

    @Builder(builderMethodName = "rankedMediaBuilder")
    public RankedMedia(Long id, String meta, Double pageRankValue) {
        super(id, meta);
        this.pageRankValue = pageRankValue;
    }

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

}
