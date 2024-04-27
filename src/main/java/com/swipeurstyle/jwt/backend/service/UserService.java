package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.dao.UserRepository;
import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public User registerNewUSer(User user){
        return userRepository.save(user);
    }

    public void initRolesAndUser() {
        User adminUser = new User();
        adminUser.setEmail("admin");
        adminUser.setPassword("admin@pass");
        adminUser.setUserRoles(Arrays.asList(UserRole.ADMINISTRADOR, UserRole.CLIENTE));
        userRepository.save(adminUser);
    }

}
