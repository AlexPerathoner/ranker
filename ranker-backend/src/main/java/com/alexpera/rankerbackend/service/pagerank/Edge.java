package com.alexpera.rankerbackend.service.pagerank;

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
