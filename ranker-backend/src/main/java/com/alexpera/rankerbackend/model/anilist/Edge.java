package com.alexpera.rankerbackend.model.anilist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge<T> {
    public T source;
    public T target;
}
