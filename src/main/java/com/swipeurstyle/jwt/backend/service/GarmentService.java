package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.dao.GarmentDao;
import com.swipeurstyle.jwt.backend.entity.Garment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarmentService {

    private final GarmentDao garmentDao;

    @Autowired
    public GarmentService(GarmentDao garmentDao){
        this.garmentDao = garmentDao;
    }
    public Garment addNewGarment(Garment garment){
        return garmentDao.save(garment);
    }

    public List<Garment> getAllGarments() {
        return (List<Garment>) garmentDao.findAll();
    }
}
