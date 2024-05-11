package com.swipeurstyle.jwt.backend.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OutfitRequest {
    private Long topId;
    private Long bottomId;
    private Long shoesId;
    private boolean scheduled;

    private LocalDate scheduledFor;
}
