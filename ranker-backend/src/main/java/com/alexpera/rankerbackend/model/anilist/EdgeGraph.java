package com.alexpera.rankerbackend.model.anilist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EdgeGraph<T> {
    private T source;
    private T target;
}
