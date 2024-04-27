package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.repository.GarmentRepository;
import com.swipeurstyle.jwt.backend.entity.Garment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarmentService {

    private final GarmentRepository garmentRepository;

    @Autowired
    public GarmentService(GarmentRepository garmentRepository){
        this.garmentRepository = garmentRepository;
    }
    public Garment addNewGarment(Garment garment){
        return garmentRepository.save(garment);
    }

    public List<Garment> getAllGarments() {
        return (List<Garment>) garmentRepository.findAll();
    }

    public List<Garment> getAllGarmentsByUser(User user) {
        return garmentRepository.findByUser(user);
    }
}
