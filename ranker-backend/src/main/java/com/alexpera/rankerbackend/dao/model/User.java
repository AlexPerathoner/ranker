package com.alexpera.rankerbackend.dao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private String id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_medias",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")) // todo find way to add pagerank_value numeric(12, 11),
    private Set<Media> medias;
}
