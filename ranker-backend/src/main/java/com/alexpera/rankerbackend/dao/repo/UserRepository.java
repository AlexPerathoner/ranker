package com.alexpera.rankerbackend.dao.repo;


import com.alexpera.rankerbackend.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findById(String id);
}
