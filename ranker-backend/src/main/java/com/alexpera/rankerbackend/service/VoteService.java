package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import com.alexpera.rankerbackend.model.anilist.VotedMedia;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class VoteService {

    @Autowired
    PageRankService pageRankService;

    public VoteService(PageRankService pageRankService) {
        this.pageRankService = pageRankService;
    }

    public List<VotedMedia> getItemsVotedAsc(String username, DistributionFunction distribution, double min, double max) {
        List<RankedMedia> items = pageRankService.getItemsSorted(username);
        List<VotedMedia> votedItems = new ArrayList<>();
        for (RankedMedia item : items) {
            double vote;
            if (distribution == DistributionFunction.LINEAR) {
                vote = (items.indexOf(item) / (double) (items.size()-1)) * (max - min) + min;
            } else if (distribution == DistributionFunction.NORMAL) {
                vote = Math.pow(2, -items.indexOf(item)); // todo implement this, add test
            } else {
                throw new IllegalArgumentException("Unknown distribution function");
            }
            votedItems.add(item.toVotedMedia(vote));
        }
        return votedItems.stream().sorted(Comparator.comparingDouble(VotedMedia::getVote)).toList();
    }
}
