package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.FileData;
import com.swipeurstyle.jwt.backend.entity.ImageData;
import com.swipeurstyle.jwt.backend.repository.FileDataRepository;
import com.swipeurstyle.jwt.backend.repository.StorageRepository;
import com.swipeurstyle.jwt.backend.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository repository;

    @Autowired
    private FileDataRepository fileDataRepository;

    public Optional<FileData> loadImageFromName(String name) {
        return fileDataRepository.findByName(name);
    }

    public String uploadImage(MultipartFile file) throws IOException {
        ImageData imageData = repository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (imageData != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }


    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = repository.findByName(fileName);
        return dbImageData.map(imageData -> ImageUtils.decompressImage(imageData.getImageData())).orElse(null);
    }



    public String deleteImage(String name) {
        Optional<ImageData> dbImageData = repository.findByName(name);
        if (dbImageData.isPresent()) {
            repository.delete(dbImageData.get());
            return "Image deleted successfully";
        } else {
            return "Image not found";
        }
    }

}