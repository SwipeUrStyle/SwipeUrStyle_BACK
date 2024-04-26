package com.swipeurstyle.jwt.backend.dao;

import com.swipeurstyle.jwt.backend.entity.Garment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentDao extends CrudRepository<Garment, Long> {

}
