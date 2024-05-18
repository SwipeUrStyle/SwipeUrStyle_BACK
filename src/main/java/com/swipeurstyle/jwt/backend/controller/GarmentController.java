package com.swipeurstyle.jwt.backend.controller;


import com.swipeurstyle.jwt.backend.entity.*;
import com.swipeurstyle.jwt.backend.repository.SessionRepository;
import com.swipeurstyle.jwt.backend.service.GarmentService;
import com.swipeurstyle.jwt.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class GarmentController {

    private final GarmentService garmentService;
    private final SessionRepository sessionRepository;
    private final StorageService storageService;

    @Autowired
    public GarmentController(GarmentService garmentService,
                             SessionRepository sessionRepository,
                             StorageService storageService) {
        this.garmentService = garmentService;
        this.sessionRepository = sessionRepository;
        this.storageService = storageService;
    }

    @PostMapping(value = {"/garment"})
    public ResponseEntity<Garment> addNewGarment(@RequestBody GarmentRequest garmentRequest, @RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        Garment garment = new Garment();
        byte[] imageData = storageService.downloadImage(garmentRequest.getImageName());
        if (imageData.length == 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        garment.setImageName(garmentRequest.getImageName());
        garment.setName(garmentRequest.getName());
        garment.setCategory(garmentRequest.getCategory());
        garment.setDescription(garmentRequest.getDescription());
        garment.setUser(user);

        return new ResponseEntity<>(garmentService.addNewGarment(garment), HttpStatus.CREATED);
    }


    @GetMapping({"/garments"})
    public ResponseEntity<List<Garment>> getAllGarments(@RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(garmentService.getAllGarmentsCreatedByUser(user), HttpStatus.FOUND);
    }

    @GetMapping({"/garments/{category}"})
    public ResponseEntity<List<Garment>> getAllGarmentsByCategory(@PathVariable String category,
                                                                  @RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        GarmentCategory garmentCategory = null;
        switch (category) {
            case "TOP":
                garmentCategory = GarmentCategory.TOP;
                break;
            case "BOTTOM":
                garmentCategory = GarmentCategory.BOTTOM;
                break;
            case "SHOES":
                garmentCategory = GarmentCategory.SHOES;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(garmentService.getAllGarmentsByCategory(garmentCategory, user), HttpStatus.FOUND);
    }

    @DeleteMapping("/garment/{id}")
    public ResponseEntity<Garment> deleteGarment(@PathVariable Long id, @RequestHeader(name = "authToken") String authToken) throws GarmentException {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = session.getUser();

        Optional<Garment> optionalGarment = Optional.ofNullable(garmentService.getGarmentByIdAndUser(id, user));
        if (!optionalGarment.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Garment garmentToDelete = optionalGarment.get();

        garmentService.deleteGarmentByUser(garmentToDelete.getId(), user);

        return new ResponseEntity<>(garmentToDelete, HttpStatus.OK);
    }

    @GetMapping({"/garments/trash"})
    public ResponseEntity<List<Garment>> getAllGarmentsDeleted(@RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(garmentService.getAllGarmentsDeletedByUser(user), HttpStatus.FOUND);
    }

    @PutMapping("/garment/restore/{id}")
    public ResponseEntity<Garment> restoreGarment(@PathVariable Long id, @RequestHeader(name = "authToken") String authToken) throws GarmentException {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = session.getUser();

        Optional<Garment> optionalGarmentDeleted = Optional.ofNullable(garmentService.getGarmentByIdAndUser(id, user));
        if (!optionalGarmentDeleted.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Garment garmentDeleted = optionalGarmentDeleted.get();

        return new ResponseEntity<>(garmentService.restoreGarment(garmentDeleted.getId(), user), HttpStatus.OK);
    }


}
