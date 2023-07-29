package com.alexpera.rankerbackend.model.anilist;

import com.alexpera.rankerbackend.dao.model.Media;
import lombok.*;

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
        // ignoring page rank value as it would change the key of the hashmap used in the graph,
        // causing it to not find the item in the graph anymore
        return getId().compareTo(o.getId());
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof RankedMedia item) {
            return Objects.equals(this.getId(), item.getId());
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        // ignoring pagerank value for same reasons as above
        return super.hashCode();
    }

    public VotedMedia toVotedMedia(double vote) {
        if (this instanceof VotedMedia) {
            return (VotedMedia) this;
        }
        return VotedMedia.votedMediaBuilder()
                .id(this.getId())
                .meta(this.getMeta())
                .pageRankValue(this.getPageRankValue())
                .vote(vote)
                .build();
    }
}
