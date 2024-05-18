package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.Session;
import com.swipeurstyle.jwt.backend.repository.SessionRepository;
import com.swipeurstyle.jwt.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {
    private StorageService service;

    private final SessionRepository sessionRepository;

    @Autowired
    public ImageController(StorageService service, SessionRepository sessionRepository) {
        this.service = service;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                         @RequestHeader(name = "authToken") String authToken) throws IOException {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        String uploadImage = service.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName,
                                           @RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        byte[] imageData = service.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }

}
