package com.swipeurstyle.jwt.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "top", cascade = CascadeType.REMOVE)
    private List<Outfit> outfitsWithTop;

    @OneToMany(mappedBy = "bottom", cascade = CascadeType.REMOVE)
    private List<Outfit> outfitsWithBottom;

    @OneToMany(mappedBy = "shoes", cascade = CascadeType.REMOVE)
    private List<Outfit> outfitsWithShoes;

    @PreRemove
    private void preRemove() {
        if (outfitsWithTop != null) {
            outfitsWithTop.forEach(outfit -> outfit.setTop(null));
        }
        if (outfitsWithBottom != null) {
            outfitsWithBottom.forEach(outfit -> outfit.setBottom(null));
        }
        if (outfitsWithShoes != null) {
            outfitsWithShoes.forEach(outfit -> outfit.setShoes(null));
        }
    }

}
