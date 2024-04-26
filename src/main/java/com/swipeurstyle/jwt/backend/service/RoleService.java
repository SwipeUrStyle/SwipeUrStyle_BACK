package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.dao.RoleDao;
import com.swipeurstyle.jwt.backend.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private RoleDao roleDao;

    @Autowired
    public RoleService(RoleDao roleDao){
        this.roleDao = roleDao;
    }
    public Role createNewRole(Role role){
        return roleDao.save(role);
    }
}
