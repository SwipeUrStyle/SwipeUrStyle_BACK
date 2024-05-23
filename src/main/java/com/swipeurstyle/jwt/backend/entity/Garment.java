package com.swipeurstyle.jwt.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "top")
    private List<Outfit> outfitsWithTop = new ArrayList<>();

    @OneToMany(mappedBy = "bottom")
    private List<Outfit> outfitsWithBottom = new ArrayList<>();

    @OneToMany(mappedBy = "shoes")
    private List<Outfit> outfitsWithShoes = new ArrayList<>();
}

