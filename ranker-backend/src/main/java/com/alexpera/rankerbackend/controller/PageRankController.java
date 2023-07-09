package com.alexpera.rankerbackend.controller;

import com.alexpera.rankerbackend.model.anilist.AnilistMedia;
import com.alexpera.rankerbackend.service.pagerank.PageRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PageRankController {
    @Autowired
    PageRankService<AnilistMedia> pageRankService;

    // todo add methods to add links
}
