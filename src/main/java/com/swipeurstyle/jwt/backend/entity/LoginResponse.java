package com.swipeurstyle.jwt.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

    private User user;
    private UUID token;

}
