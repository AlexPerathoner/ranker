package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
public class VotedMedia extends RankedMedia {
    private Double vote;

    public VotedMedia(Media media, Double vote) {
        super(media.getId(), media.getMeta(), null);
        this.vote = vote;
    }

    @Builder(builderMethodName = "votedMediaBuilder")
    public VotedMedia(Long id, String meta, Double pageRankValue, Double vote) {
        super(id, meta, pageRankValue);
        this.vote = vote;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int compareTo(Media o) {
        if (o instanceof VotedMedia media) {
            return Double.compare(this.getVote(), media.getVote());
        }
        return super.compareTo(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vote);
    }

}
