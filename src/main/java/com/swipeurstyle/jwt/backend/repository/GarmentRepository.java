package com.swipeurstyle.jwt.backend.repository;

import com.swipeurstyle.jwt.backend.entity.Garment;
import com.swipeurstyle.jwt.backend.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GarmentRepository extends CrudRepository<Garment, Long> {
    List<Garment> findByUser(User user);
}
