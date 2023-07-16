package com.alexpera.rankerbackend.model.anilist;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public
class RankedAnilistMedia {
    AnilistMedia anilistMedia;
    double rank;
}