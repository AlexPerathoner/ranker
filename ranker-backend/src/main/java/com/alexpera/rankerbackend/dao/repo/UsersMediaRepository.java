package com.alexpera.rankerbackend.dao.repo;

import com.alexpera.rankerbackend.dao.model.UsersMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;


public interface UsersMediaRepository extends JpaRepository<UsersMedia, Long> {
    Set<UsersMedia> findAllByUserId(String userId);
}
