package com.alexpera.rankerbackend.dao.model;


import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findById(String id);
}
