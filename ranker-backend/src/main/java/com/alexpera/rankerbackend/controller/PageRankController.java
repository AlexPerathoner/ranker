package com.alexpera.rankerbackend.controller;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.Edge;
import com.alexpera.rankerbackend.service.pagerank.PageRankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Log4j2
@CrossOrigin(maxAge = 3600)
@Controller
public class PageRankController {
    @Autowired
    PageRankService<AnilistMedia> pageRankService;

    @GetMapping("/load-file")
    public ResponseEntity<String> loadFile(@RequestParam String username) throws IOException {
        File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("completedSeriesSmall.json")).getFile()
        );
        ObjectMapper mapper = new ObjectMapper();
        AnilistMedia[] anilistMedias = mapper.readValue(file, AnilistMedia[].class);
        ArrayList<AnilistMedia> anilistMediaArrayList = new ArrayList<>(Arrays.asList(anilistMedias));
        pageRankService.addAll(username, anilistMediaArrayList);
        return ResponseEntity.ok().body("File loaded");
    }

    @GetMapping("/get-items-sorted")
    public ResponseEntity<List<AnilistMedia>> getItemsSorted(@RequestParam String username) {
        return ResponseEntity.ok().body(pageRankService.getItemsSorted(username));
    }

    @GetMapping("/load-series")
    public ResponseEntity loadSeries(@RequestParam String username) {
        return ResponseEntity.ok().build();
    }


    @GetMapping("/get-items-ranked")
    public ResponseEntity<List<AnilistMedia>> getItemsRanked(@RequestParam String username) {
        return ResponseEntity.ok().body(pageRankService.getItemsRanked(username, DistributionFunction.constant));
    }

    @GetMapping("/get-next-comparison")
    public ResponseEntity<Set<AnilistMedia>> getNextComparison(@RequestParam String username) {
        return ResponseEntity.ok().body(pageRankService.getNextComparison(username));
    }

    @GetMapping("/add-link")
    public ResponseEntity<String> addLink(@RequestParam String username, @RequestParam long betterId, @RequestParam long worseId) {
        Set<AnilistMedia> items = pageRankService.getItems(username);
        AnilistMedia better = items.stream().filter(item -> item.getId() == betterId).findFirst().orElse(null);
        AnilistMedia worse = items.stream().filter(item -> item.getId() == worseId).findFirst().orElse(null);
        if (better == null || worse == null) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        pageRankService.addLink(username, better, worse);
        return ResponseEntity.ok().body("Link added");
    }

    @GetMapping("/calculate-iteration")
    public ResponseEntity<List<AnilistMedia>> calculateIteration(@RequestParam String username) {
        pageRankService.calculateIteration(username);
        return ResponseEntity.ok().body(pageRankService.getItemsSorted(username));
    }

    @GetMapping("/save")
    public ResponseEntity<String> save(@RequestParam String username) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("completedSeries-"+ new Date().getTime() +".json"), pageRankService.getItemsSorted(username));
        return ResponseEntity.ok().body("Saved");
    }

    @GetMapping("/edges-id")
    public ResponseEntity<List<Edge<Long>>> edgesId(@RequestParam String username) {
        List<Edge<AnilistMedia>> edges = pageRankService.getEdges(username).stream().toList();

        List<Edge<Long>> edgesId = edges.stream().map(edge -> new Edge<>(edge.getSource().getId(), edge.getTarget().getId())).toList();
        return ResponseEntity.ok().body(edgesId);
    }
    @GetMapping("/edges")
    public ResponseEntity<List<Edge<AnilistMedia>>> edges(@RequestParam String username) {
        List<Edge<AnilistMedia>> edges = pageRankService.getEdges(username).stream().toList();

        return ResponseEntity.ok().body(edges);
    }
}
