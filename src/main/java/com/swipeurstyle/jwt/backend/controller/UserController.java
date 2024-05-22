package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.Gender;
import com.swipeurstyle.jwt.backend.entity.Session;
import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.exception.UserNotFoundException;
import com.swipeurstyle.jwt.backend.repository.SessionRepository;
import com.swipeurstyle.jwt.backend.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;
    private final SessionRepository sessionRepository;

    @Autowired
    public UserController(UserService userService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @PostConstruct
    public void initRolesAndUsers() {
        userService.initRolesAndUser();
    }

    @PostMapping({"/user/add"})
    public User registerNewUSer(@RequestBody User user) {
        return userService.registerNewUSer(user);
    }

    @PatchMapping("/user")
    public ResponseEntity<User> partialUpdateUser(
            @RequestBody Map<String, Object> updates,
            @RequestHeader(name = "authToken") String authToken
    ) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = session.getUser();

        try {
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                switch (key) {
                    case "name":
                        user.setName((String) value);
                        break;
                    case "gender":
                        Gender gender;
                        switch (((String) value).toUpperCase()) {
                            case "MALE":
                                gender = Gender.MALE;
                                break;
                            case "FEMALE":
                                gender = Gender.FEMALE;
                                break;
                            case "NON_BINARY":
                                gender = Gender.NON_BINARY;
                                break;
                            case "OTHER":
                                gender = Gender.OTHER;
                                break;
                            case "UNSPECIFIED":
                                gender = Gender.UNSPECIFIED;
                                break;
                            default:
                                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                        user.setGender(gender);
                        break;
                    default:
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            User userUpdated = userService.updateUser(user);
            return ResponseEntity.ok(userUpdated);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
