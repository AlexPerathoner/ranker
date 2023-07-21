package com.alexpera.rankerbackend.dao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users_medias")
public class UsersMedia {
    @EmbeddedId
    private UsersMediaId id = new UsersMediaId();

    @ManyToOne
    @MapsId("user_id")
    private User user;

    @ManyToOne
    @MapsId("media_id")
    private Media media;

    @Column(name = "pagerank_value")
    private Double pagerankValue;

}
