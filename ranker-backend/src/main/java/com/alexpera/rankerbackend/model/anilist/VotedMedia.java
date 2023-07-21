package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
public class VotedMedia extends RankedMedia {
     private Double vote;

    public VotedMedia(Media media) {
        super(media);
    }
    public VotedMedia(Media media, Double vote) {
        super(media);
        this.vote = vote;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof VotedMedia item) {
            return Objects.equals(this.getVote(), item.getVote());
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vote);
    }
}
