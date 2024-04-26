package com.swipeurstyle.jwt.backend.dao;

import com.swipeurstyle.jwt.backend.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends CrudRepository<Role, String > {
}
