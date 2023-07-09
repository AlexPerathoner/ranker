package com.alexpera.rankerbackend.controller;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.service.pagerank.PageRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Set;

@Controller
public class PageRankController {
    @Autowired
    PageRankService<AnilistMedia> pageRankService;

    // todo add methods to add links
    // add POST method to add link
    @GetMapping("/addLink")
    public void loadFile() {
        //  parse json file in resources


    }
    @GetMapping("/getNextComparison")
    public Set<AnilistMedia> getNextComparison() {
        return pageRankService.getNextComparison();
    }
}
