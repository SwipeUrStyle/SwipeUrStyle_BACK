package com.swipeurstyle.jwt.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Outfit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "top_id")
    private Garment top;

    @ManyToOne
    @JoinColumn(name = "bottom_id")
    private Garment bottom;

    @ManyToOne
    @JoinColumn(name = "shoes_id")
    private Garment shoes;

    @ManyToOne
    private User user;

    private LocalDate createdAt;

    private boolean scheduled = false;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledFor;

    private boolean favorite;


}
