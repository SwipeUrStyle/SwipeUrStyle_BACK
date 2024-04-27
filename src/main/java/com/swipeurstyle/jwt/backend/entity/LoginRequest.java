package com.swipeurstyle.jwt.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String userEmail;
    private String userPassword;


}
