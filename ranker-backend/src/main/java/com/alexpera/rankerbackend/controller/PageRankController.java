package com.alexpera.rankerbackend.controller;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.model.anilist.DistributionFunction;
import com.alexpera.rankerbackend.model.anilist.EdgeGraph;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import com.alexpera.rankerbackend.model.anilist.VotedMedia;
import com.alexpera.rankerbackend.service.PageRankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.jgrapht.graph.DefaultEdge;
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
@CrossOrigin(origins = "http://localhost:8000")
@Controller
public class PageRankController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PageRankService pageRankService;

    @GetMapping("/load-user")
    public ResponseEntity<Set<DefaultEdge>> loadUser(@RequestParam String username) {
        return ResponseEntity.ok().body(pageRankService.loadUser(username));
    }

    @GetMapping("/test-repo")
    public ResponseEntity<Set<Media>> testRepo() {
        return ResponseEntity.ok().body(userRepository.findById("Piede").getMedias());
    }

    @GetMapping("/load-file")
    public ResponseEntity<String> loadFile(@RequestParam String username) throws IOException {
        File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("completedSeriesSmall.json")).getFile()
        );
        ObjectMapper mapper = new ObjectMapper();
        RankedMedia[] anilistMedias = mapper.readValue(file, RankedMedia[].class);
        ArrayList<RankedMedia> anilistMediaArrayList = new ArrayList<>(Arrays.asList(anilistMedias));
        pageRankService.addAll(username, anilistMediaArrayList);
        return ResponseEntity.ok().body("File loaded");
    }

    @GetMapping("/get-items-sorted")
    public ResponseEntity<List<RankedMedia>> getItemsSorted(@RequestParam String username) {
        return ResponseEntity.ok().body(pageRankService.getItemsSorted(username));
    }

    @GetMapping("/load-series")
    public ResponseEntity<String> loadSeries(@RequestParam String username) {
        return ResponseEntity.ok().body("Loaded series");
    }


    @GetMapping("/get-items-ranked")
    public ResponseEntity<List<VotedMedia>> getItemsRanked(@RequestParam String username) {
        return ResponseEntity.ok().body(pageRankService.getItemsVoted(username, DistributionFunction.LINEAR));
    }

    @GetMapping("/get-next-comparison")
    public ResponseEntity<Set<Media>> getNextComparison(@RequestParam String username) {
        return ResponseEntity.ok().body(pageRankService.getNextComparison(username));
    }

    @GetMapping("/add-link")
    public ResponseEntity<String> addLink(@RequestParam String username, @RequestParam long betterId, @RequestParam long worseId) {
        Set<RankedMedia> items = pageRankService.getItems(username);
        RankedMedia better = items.stream().filter(item -> item.getId() == betterId).findFirst().orElse(null);
        RankedMedia worse = items.stream().filter(item -> item.getId() == worseId).findFirst().orElse(null);
        if (better == null || worse == null) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        pageRankService.addLink(username, better, worse);
        return ResponseEntity.ok().body("Link added");
    }

    @GetMapping("/calculate-iteration")
    public ResponseEntity<List<RankedMedia>> calculateIteration(@RequestParam String username) {
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
    public ResponseEntity<List<EdgeGraph<Long>>> edgesId(@RequestParam String username) {
        List<EdgeGraph<RankedMedia>> edges = pageRankService.getEdges(username).stream().toList();

        List<EdgeGraph<Long>> edgesId = edges.stream().map(edge -> new EdgeGraph<>(edge.getSource().getId(), edge.getTarget().getId())).toList();
        return ResponseEntity.ok().body(edgesId);
    }
    @GetMapping("/edges")
    public ResponseEntity<List<EdgeGraph<RankedMedia>>> edges(@RequestParam String username) {
        List<EdgeGraph<RankedMedia>> edges = pageRankService.getEdges(username).stream().toList();

        return ResponseEntity.ok().body(edges);
    }
}
