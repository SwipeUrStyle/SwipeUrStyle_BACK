package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.repository.SessionRepository;
import com.swipeurstyle.jwt.backend.entity.*;
import com.swipeurstyle.jwt.backend.service.GarmentService;
import com.swipeurstyle.jwt.backend.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class GarmentController {

    private final GarmentService garmentService;
    private final SessionRepository sessionRepository;
    private final StorageService storageService;

    @Autowired
    public GarmentController(GarmentService garmentService, SessionRepository sessionRepository, StorageService storageService) {
        this.garmentService = garmentService;
        this.sessionRepository = sessionRepository;
        this.storageService = storageService;
    }

    @PostMapping(value = {"/garment"})
    public ResponseEntity<Garment> addNewGarment(@RequestBody GarmentRequest garmentRequest, @CookieValue(name = "authToken") String authToken){
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        Garment garment = new Garment();
        Optional<FileData> image = storageService.loadImageFromName(garmentRequest.getImageName());
        if (image.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        garment.setImageName(image.get().getName());
        garment.setName(garmentRequest.getName());
        garment.setCategory(garmentRequest.getCategory());
        garment.setDescription(garmentRequest.getDescription());
        garment.setUser(user);

        return new ResponseEntity<>(garmentService.addNewGarment(garment), HttpStatus.CREATED);
    }


    @GetMapping({"/garments"})
    public ResponseEntity<List<Garment>> getAllGarments(@CookieValue(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(garmentService.getAllGarmentsByUser(user), HttpStatus.FOUND);
    }
}
