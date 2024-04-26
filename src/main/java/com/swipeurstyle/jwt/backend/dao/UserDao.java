package com.swipeurstyle.jwt.backend.dao;

import com.swipeurstyle.jwt.backend.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, String> {

}
