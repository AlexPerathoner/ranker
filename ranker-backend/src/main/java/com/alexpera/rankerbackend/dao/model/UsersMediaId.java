package com.alexpera.rankerbackend.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class UsersMediaId implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "media_id")
    private Long mediaId;
}