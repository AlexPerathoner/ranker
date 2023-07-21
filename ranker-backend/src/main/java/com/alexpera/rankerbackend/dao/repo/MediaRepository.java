package com.alexpera.rankerbackend.dao.repo;


import com.alexpera.rankerbackend.dao.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MediaRepository extends JpaRepository<Media, Long> {
    Optional<Media> findById(Long id);
}
