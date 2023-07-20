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
@Table(name = "medias")
public class Media {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "meta")
    private String meta;
}
