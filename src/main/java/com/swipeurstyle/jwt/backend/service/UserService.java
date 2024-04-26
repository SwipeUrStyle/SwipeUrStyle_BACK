package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.dao.RoleDao;
import com.swipeurstyle.jwt.backend.dao.UserDao;
import com.swipeurstyle.jwt.backend.entity.Role;
import com.swipeurstyle.jwt.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private UserDao userDao;
    private RoleDao roleDao;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder){
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }
    public User registerNewUSer(User user){
        return userDao.save(user);
    }

    public void initRolesAndUser() {
        Role adminRole = new Role();
        adminRole.setRoleName("Admin");
        adminRole.setRoleDescription("Admin role");
        roleDao.save(adminRole);

        Role userRole = new Role();
        userRole.setRoleName("User");
        userRole.setRoleDescription("Default role for newly created record");
        roleDao.save(userRole);

        User adminUser = new User();
        adminUser.setUserFirstName("admin");
        adminUser.setUserLastName("admin");
        adminUser.setUserName("admin123");
        adminUser.setUserPassword(getEncodedPassword("admin@pass"));
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);
        userDao.save(adminUser);

        User user = new User();
        user.setUserFirstName("juli");
        user.setUserLastName("bricen");
        user.setUserName("juli123");
        user.setUserPassword(getEncodedPassword("juli@pass"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        user.setRole(userRoles);
        userDao.save(user);
    }

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
