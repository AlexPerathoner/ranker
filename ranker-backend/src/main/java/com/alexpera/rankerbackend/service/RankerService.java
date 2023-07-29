package com.alexpera.rankerbackend.service;

import com.alexpera.rankerbackend.dao.model.Media;
import com.alexpera.rankerbackend.dao.model.User;
import com.alexpera.rankerbackend.dao.model.UsersMedia;
import com.alexpera.rankerbackend.dao.model.UsersMediaId;
import com.alexpera.rankerbackend.dao.repo.MediaRepository;
import com.alexpera.rankerbackend.dao.repo.UserRepository;
import com.alexpera.rankerbackend.dao.repo.UsersMediaRepository;
import com.alexpera.rankerbackend.exceptions.EmptyGraphException;
import com.alexpera.rankerbackend.exceptions.FullGraphException;
import com.alexpera.rankerbackend.exceptions.LoopException;
import com.alexpera.rankerbackend.model.anilist.EdgeGraph;
import com.alexpera.rankerbackend.model.anilist.RankedMedia;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RankerService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UsersMediaRepository usersMediaRepository;
    @Autowired
    MediaRepository mediaRepository;
    @Autowired
    UserService userService;

    public RankerService(UserRepository userRepository, UsersMediaRepository usersMediaRepository, MediaRepository mediaRepository, UserService userService) {
        this.userRepository = userRepository;
        this.usersMediaRepository = usersMediaRepository;
        this.mediaRepository = mediaRepository;
        this.userService = userService;
    }

    private Graph<RankedMedia, DefaultEdge> getGraph(String username) {
        return userService.getGraph(username);
    }


    public Set<Media> getNextComparison(String username) throws FullGraphException, EmptyGraphException {
        // todo change, shouldnt return two media if there is already an edge between them

        // todo rewrite consdering that it should behave like a binary search:
        //      a b c d e f g
        // first compare with d, then f, then e / g -> also depends on existing edges (consider them)
        // first part to find first item of edge is good: consider item with least total count of edges
        // find item with lowest number of incoming or outgoing edges
        RankedMedia minItem = null;
        int minEdges = Integer.MAX_VALUE;
        Graph<RankedMedia, DefaultEdge> graph = getGraph(username);
        for (RankedMedia item : graph.vertexSet()) {
            int edges = graph.inDegreeOf(item) + getGraph(username).outDegreeOf(item);
            if (edges < minEdges) {
                minEdges = edges;
                minItem = item;
            }
        }
        // check outgoing edges of minItem. If # = vertex # * 2, graph is complete (loops are allowed)

        // todo refactor
        // get mid item
        RankedMedia finalMinItem = minItem;
        List<RankedMedia> items = getItemsSorted(username).stream()
                .filter(t -> graph.containsEdge(finalMinItem,t))
                .toList();

        if (items.isEmpty()) {
            throw new FullGraphException("All possible comparison for user " + username + " have already been made");
        }

        int index = items.size() / 2;
        RankedMedia midItem = items.get(index);

        if (minItem == null) {
            throw new EmptyGraphException("No items in graph for user " + username + " to compare.");
        }
        int vertexCount = graph.vertexSet().size();
        return Set.of(minItem, midItem);
    }

    private void saveMediaOfUser(String username) { // todo should get called before shutdown
        User user = userRepository.findById(username);
        getGraph(username).vertexSet().forEach(media -> {
            UsersMediaId id = new UsersMediaId(user.getId(), media.getId());
            usersMediaRepository.save(new UsersMedia(id, user, media, media.getPageRankValue()));
        });

    }
    public void addLink(String username, Media worse, Media better) {
        if (worse.equals(better)) {
            throw new LoopException("Cannot add link to itself.");
        }
        getGraph(username).addEdge(worse.toRankedMedia(), better.toRankedMedia());
    }


    public Set<EdgeGraph<RankedMedia>> getEdges(String username) { // todo move to userservice?
        Set<DefaultEdge> edges = getGraph(username).edgeSet();
        HashSet<EdgeGraph<RankedMedia>> result = new HashSet<>();
        for (DefaultEdge edge : edges) {
            result.add(new EdgeGraph<>(getGraph(username).getEdgeSource(edge), getGraph(username).getEdgeTarget(edge)));
        }
        return result;
    }

    public List<RankedMedia> getItemsSorted(String username) {
        return userService.getItems(username).stream().sorted().toList();
    }

}