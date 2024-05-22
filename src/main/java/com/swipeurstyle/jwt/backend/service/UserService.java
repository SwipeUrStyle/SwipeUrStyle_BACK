package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.Gender;
import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.entity.UserRole;
import com.swipeurstyle.jwt.backend.exception.UserNotFoundException;
import com.swipeurstyle.jwt.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerNewUSer(User user) {
        return userRepository.save(user);
    }

    public void initRolesAndUser() {
        User adminUser = new User();
        adminUser.setEmail("admin@gmail.com");
        adminUser.setPassword("admin@pass");
        adminUser.setUsername("admin");
        adminUser.setName("Admin");
        adminUser.setUserRoles(Arrays.asList(UserRole.ADMINISTRADOR, UserRole.CLIENTE));
        userRepository.save(adminUser);

        User user = new User();
        user.setEmail("jorge-u@gmail.com");
        user.setPassword("jorge@pass");
        user.setUsername("juusechec");
        user.setName("Jorge Ulises");
        user.setGender(Gender.MALE);
        user.setUserRoles(List.of(UserRole.CLIENTE));
        userRepository.save(user);
    }

    public User updateUser(User user) throws UserNotFoundException {
        Optional<User> userToUpdate = userRepository.findById(user.getEmail());
        if (userToUpdate.isPresent()) {
            return userRepository.save(user);
        } else {
            throw new UserNotFoundException("User with email " + user.getEmail() + " not found");
        }
    }


}
