package com.alexpera.rankerbackend.controller;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.service.pagerank.DistributionFunction;
import com.alexpera.rankerbackend.service.pagerank.PageRankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Log4j2
@Controller
public class PageRankController {
    @Autowired
    PageRankService<AnilistMedia> pageRankService;

    @GetMapping("/load-file")
    public ResponseEntity<String> loadFile() throws IOException {
        File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("completedSeriesSmall.json")).getFile()
        );
        ObjectMapper mapper = new ObjectMapper();
        AnilistMedia[] anilistMedias = mapper.readValue(file, AnilistMedia[].class);
        ArrayList<AnilistMedia> anilistMediaArrayList = new ArrayList<>(Arrays.asList(anilistMedias));
        pageRankService.addAll(anilistMediaArrayList);
        return ResponseEntity.ok().body("File loaded");
    }

    @GetMapping("/get-items-sorted")
    public ResponseEntity<List<AnilistMedia>> getItemsSorted() {
        return ResponseEntity.ok().body(pageRankService.getItemsSorted());
    }

    @GetMapping("/get-items-ranked")
    public ResponseEntity<List<AnilistMedia>> getItemsRanked() {
        return ResponseEntity.ok().body(pageRankService.getItemsRanked(DistributionFunction.constant));
    }

    @GetMapping("/get-next-comparison")
    public ResponseEntity<Set<AnilistMedia>> getNextComparison() {
        return ResponseEntity.ok().body(pageRankService.getNextComparison());
    }

    @GetMapping("/add-link")
    public ResponseEntity<String> addLink(@RequestParam long betterId, @RequestParam long worseId) {
        AnilistMedia better = pageRankService.getItems().stream().filter(item -> item.getId() == betterId).findFirst().orElse(null);
        AnilistMedia worse = pageRankService.getItems().stream().filter(item -> item.getId() == worseId).findFirst().orElse(null);
        if (better == null || worse == null) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        pageRankService.addLink(better, worse);
        return ResponseEntity.ok().body("Link added");
    }

    @GetMapping("/calculate-iteration")
    public ResponseEntity<String> calculateIteration() {
        pageRankService.calculateIteration();
        return ResponseEntity.ok().body("Iteration calculated");
    }

    @GetMapping("/save")
    public ResponseEntity<String> save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("completedSeries-"+ new Date().getTime() +".json"), pageRankService.getItemsSorted());
        return ResponseEntity.ok().body("Saved");
    }
}
