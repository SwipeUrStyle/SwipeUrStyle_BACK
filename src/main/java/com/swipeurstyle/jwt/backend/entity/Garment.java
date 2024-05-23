package com.swipeurstyle.jwt.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Garment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private GarmentCategory category;
    private String imageName;
    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private GarmentState garmentState = GarmentState.CREATED;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    private boolean enabled = true;
}

