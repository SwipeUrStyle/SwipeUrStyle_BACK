package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.Role;
import com.swipeurstyle.jwt.backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {

    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping({"/role/add"})
    public Role createNewRole(@RequestBody Role role){
        return roleService.createNewRole(role);
    }
}
