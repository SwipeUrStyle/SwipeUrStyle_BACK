package com.swipeurstyle.jwt.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GarmentRequest {
    private String name;
    private String description;
    private GarmentCategory category;
    private String imageName;
}
