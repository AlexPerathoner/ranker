package com.alexpera.rankerbackend;

import com.alexpera.rankerbackend.dao.model.Media;

public class CommonMethods {
    public static Media createMedia(Long id) {
        return Media.builder()
                .id(id)
                .build();
    }
}
