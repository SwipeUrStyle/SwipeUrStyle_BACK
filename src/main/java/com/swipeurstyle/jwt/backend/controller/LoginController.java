package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.LoginRequest;
import com.swipeurstyle.jwt.backend.entity.LoginResponse;
import com.swipeurstyle.jwt.backend.entity.Session;
import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.repository.SessionRepository;
import com.swipeurstyle.jwt.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.UUID;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    private static final String LOGIN_PAGE = "login/login";

    private final UserRepository userRepository;

    private final SessionRepository sessionRepository;

    @Autowired
    public LoginController(
            UserRepository userRepository,
            SessionRepository sessionRepository
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("")
    public ResponseEntity<LoginResponse> loginSubmit(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getUserEmail());
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else if (!user.getPassword().equals(loginRequest.getUserPassword())) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } else {
            UUID token = UUID.randomUUID();
            Session session = new Session(token, Instant.now(), user);
            sessionRepository.save(session);
            return new ResponseEntity<>(new LoginResponse(user, token), HttpStatus.FORBIDDEN);
        }
    }

}
