package com.swipeurstyle.jwt.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "user")
public class User {

    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "roles", nullable = false)
    private List<UserRole> userRoles;

    @Column(unique = true)
    private String username;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNSPECIFIED;

}
