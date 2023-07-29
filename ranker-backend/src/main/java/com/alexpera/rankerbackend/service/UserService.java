package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.repo.EdgeRepository;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class UserService {
    @Autowired
    EdgeRepository edgeRepository;
    @Autowired
    MediaRepository mediaRepository;
    @Autowired
    UsersMediaRepository usersMediaRepository;
    @Autowired
    AnilistService anilistService;
    @Autowired
    RankerService rankerService;

    public UserService(EdgeRepository edgeRepository, MediaRepository mediaRepository, UsersMediaRepository usersMediaRepository, AnilistService anilistService, RankerService rankerService) {
        this.edgeRepository = edgeRepository;
        this.mediaRepository = mediaRepository;
        this.usersMediaRepository = usersMediaRepository;
        this.anilistService = anilistService;
        this.rankerService = rankerService;
    }

    @Getter
    private final HashMap<String, Graph<RankedMedia, DefaultEdge>> usersGraph = new HashMap<>();

    public Graph<RankedMedia, DefaultEdge> getGraph(String username) {
        usersGraph.computeIfAbsent(username, k -> new DefaultDirectedGraph<>(DefaultEdge.class));
        return usersGraph.get(username);
    }

    public Set<RankedMedia> getItems(String username) {
        return getGraph(username).vertexSet();
    }


    public void add(String username, RankedMedia item) {
        if (!getGraph(username).containsVertex(item)) {
            getGraph(username).addVertex(item);
        }
    }


    public void addAll(String username, List<RankedMedia> items) {
        items.forEach(anilistMedia -> add(username, anilistMedia));
    }


    public Set<DefaultEdge> loadUser(String username) {
        return loadUser(username, true);
    }

    public Set<DefaultEdge> loadUser(String username, boolean mergeAnilist) {
        if (usersGraph.containsKey(username)) {
            return new HashSet<>();
        }

        Graph<RankedMedia, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        loadVertex(username, mergeAnilist, graph);
        loadEdges(username, graph);

        usersGraph.put(username, graph); // todo low priority: add compatibility for concurrency
        return graph.edgeSet();
    }

    private void loadEdges(String username, Graph<RankedMedia, DefaultEdge> graph) {
        edgeRepository.findByUsername(username).forEach(edge -> graph
                .addEdge(edge.getBetter().toRankedMedia(),edge.getWorse().toRankedMedia()));
    }

    private void loadVertex(String username, boolean mergeAnilist, Graph<RankedMedia, DefaultEdge> graph) {
        usersMediaRepository.findAllByUserId(username).forEach(
                usersMedia -> {
                    RankedMedia rankedMedia = usersMedia.getMedia().toRankedMedia(usersMedia.getPagerankValue());
                    graph.addVertex(rankedMedia);
                }
        );
        AtomicReference<Boolean> newItemsFound = new AtomicReference<>(true);
        if (mergeAnilist) {
            List<Media> anilistItems = anilistService.retrieveCompletedMedia(username);

            anilistItems.forEach(
                    media -> {
                        RankedMedia rankedMedia = media.toRankedMedia();
                        if(!newItemsFound.get() && mediaRepository.findById(rankedMedia.getId()).isPresent()) {
                            newItemsFound.set(true);
                        }
                        graph.addVertex(rankedMedia);
                    }
            );
            rankerService.saveMediaToDB(anilistItems);
        }

        if (newItemsFound.get()) {
            PageRankService.resetPageRankToInitialValue(graph);
        }
    }
}
