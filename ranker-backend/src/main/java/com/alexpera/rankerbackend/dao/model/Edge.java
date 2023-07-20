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
@Table(name = "edges")
public class Edge {

    @ManyToOne
    @JoinColumn(name = "better_id")
    private Media better;

    @ManyToOne
    @JoinColumn(name = "worse_id")
    private Media worse;

    @Id
    @JoinColumn(name = "username")
    private String username;
}
