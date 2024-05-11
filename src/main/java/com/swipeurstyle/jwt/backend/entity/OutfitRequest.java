package com.swipeurstyle.jwt.backend.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OutfitRequest {
    private Long topId;
    private Long bottomId;
    private Long shoesId;
    private boolean scheduled;

    private LocalDateTime scheduledFor;
}
