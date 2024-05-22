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
    private final StorageService service;

    private final SessionRepository sessionRepository;

    @Autowired
    public ImageController(StorageService service, SessionRepository sessionRepository) {
        this.service = service;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file,
                                         @RequestHeader(name = "authToken") String authToken) throws IOException {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String uploadImage = service.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String fileName,
                                           @RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        byte[] imageData = service.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> deleteImage(@PathVariable String fileName,
                                         @RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String response = service.deleteImage(fileName);
        if ("Image deleted successfully".equals(response)) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}
