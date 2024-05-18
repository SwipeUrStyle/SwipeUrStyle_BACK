package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void initRolesAndUsers() {
        userService.initRolesAndUser();
    }

    @PostMapping({"/user/add"})
    public User registerNewUSer(@RequestBody User user) {
        return userService.registerNewUSer(user);
    }

    @GetMapping({"/forAdmin"})
    public String forAdmin() {
        return "This URL is only accessible to admin";
    }

    @GetMapping({"/forUser"})
    public String forUser() {
        return "This URL is only accessible to the user";
    }
}
