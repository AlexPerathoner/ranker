package com.alexpera.rankerbackend.dao.repo;


import com.alexpera.rankerbackend.dao.model.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;


public interface EdgeRepository extends JpaRepository<Edge, Long> {
    Set<Edge> findByUsername(String username);
}
