package com.alexpera.rankerbackend.controller;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.service.pagerank.PageRankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("completedSeries.json")).getFile()
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

    @GetMapping("/getNextComparison")
    public Set<AnilistMedia> getNextComparison() {
        return pageRankService.getNextComparison();
    }
}
