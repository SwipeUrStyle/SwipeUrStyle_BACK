package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.dao.SessionRepository;
import com.swipeurstyle.jwt.backend.entity.*;
import com.swipeurstyle.jwt.backend.service.GarmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class GarmentController {

    private final GarmentService garmentService;
    private final SessionRepository sessionRepository;

    @Autowired
    public GarmentController(GarmentService garmentService, SessionRepository sessionRepository) {
        this.garmentService = garmentService;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping(value = {"/garment"})
    public Garment addNewGarment(@RequestBody GarmentRequest garmentRequest, @CookieValue(name = "authToken") String authToken){
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        User user = session.getUser();
        // return garmentService.addNewGarment(garment);
        Garment garment = new Garment();
        garment.setName(garmentRequest.getName());
        garment.setCategory(garmentRequest.getCategory());
        garment.setDescription(garmentRequest.getDescription());
        // ImageModel image = imageRepository.getById(garmentRequest.getImageId())
        // garment.setGarmentImage(image);
        try {
            return garmentService.addNewGarment(garment);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping(value = {"/image"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Garment addNewGarment(@RequestPart("imageFile") MultipartFile file){
        try {
            ImageModel image = uploadImage(file);
            //garment.setGarmentImage(image);
            //return imageSErevice.addNewImage(garment);
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ImageModel uploadImage(MultipartFile multipartFile) throws IOException {
        return new ImageModel(
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType(),
                multipartFile.getBytes()
        );
    }

    @GetMapping({"/garments"})
    public List<Garment> getAllGarments() {
        return garmentService.getAllGarments();
    }
}
