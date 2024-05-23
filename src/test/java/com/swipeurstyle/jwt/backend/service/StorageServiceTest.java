package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.FileData;
import com.swipeurstyle.jwt.backend.entity.ImageData;
import com.swipeurstyle.jwt.backend.repository.FileDataRepository;
import com.swipeurstyle.jwt.backend.repository.StorageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageRepository storageRepositoryMock;

    @Mock
    private FileDataRepository fileDataRepositoryMock;

    @InjectMocks
    private StorageService storageService;

    @Test
    void testLoadImageFromName() {
        // Simular el comportamiento del repositorio
        when(fileDataRepositoryMock.findByName("test.jpg")).thenReturn(Optional.of(new FileData()));

        // Llamar al método del servicio que quieres probar
        Optional<FileData> imageData = storageService.loadImageFromName("test.jpg");

        // Verificar el resultado
        assertTrue(imageData.isPresent());
    }

    @Test
    void testUploadImage() throws IOException {
        // Crear un archivo multipart de ejemplo
        MultipartFile multipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test".getBytes());

        // Simular el comportamiento del repositorio
        when(storageRepositoryMock.save(any(ImageData.class))).thenReturn(new ImageData());

        // Llamar al método del servicio que quieres probar
        String result = storageService.uploadImage(multipartFile);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals("file uploaded successfully : test.jpg", result);
    }


    @Test
    void testDeleteImage_ImageFound() {
        // Simular el comportamiento del repositorio
        when(storageRepositoryMock.findByName("test.jpg")).thenReturn(Optional.of(new ImageData()));

        // Llamar al método del servicio que quieres probar
        String result = storageService.deleteImage("test.jpg");

        // Verificar el resultado
        assertNotNull(result);
        assertEquals("Image deleted successfully", result);
    }

    @Test
    void testDeleteImage_ImageNotFound() {
        // Simular el comportamiento del repositorio
        when(storageRepositoryMock.findByName("test.jpg")).thenReturn(Optional.empty());

        // Llamar al método del servicio que quieres probar
        String result = storageService.deleteImage("test.jpg");

        // Verificar el resultado
        assertNotNull(result);
        assertEquals("Image not found", result);
    }

}

