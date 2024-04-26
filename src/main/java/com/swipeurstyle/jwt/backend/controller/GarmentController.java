package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.Garment;
import com.swipeurstyle.jwt.backend.entity.ImageModel;
import com.swipeurstyle.jwt.backend.service.GarmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class GarmentController {

    private final GarmentService garmentService;

    @Autowired
    public GarmentController(GarmentService garmentService){
        this.garmentService = garmentService;
    }

    @PostMapping(value = {"/garment/add"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Garment addNewGarment(@RequestPart("garment") Garment garment,
                                 @RequestPart("imageFile")MultipartFile file){
    //    return garmentService.addNewGarment(garment);
        try {
            ImageModel image = uploadImage(file);
            garment.setGarmentImage(image);
            return garmentService.addNewGarment(garment);
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
