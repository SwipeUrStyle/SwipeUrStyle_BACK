package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.Garment;
import com.swipeurstyle.jwt.backend.entity.GarmentCategory;
import com.swipeurstyle.jwt.backend.entity.GarmentState;
import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.exception.GarmentException;
import com.swipeurstyle.jwt.backend.repository.GarmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarmentServiceTest {

    @Mock
    private GarmentRepository garmentRepositoryMock;

    @Mock
    private StorageService storageServiceMock;

    @InjectMocks
    private GarmentService garmentService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
    }

    @Test
    public void testAddNewGarment() {
        // Crear un garment de ejemplo
        Garment garment = new Garment();
        garment.setId(1L);
        garment.setName("Test Garment");
        garment.setUser(user);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.save(garment)).thenReturn(garment);

        // Llamar al método del servicio que quieres probar
        Garment addedGarment = garmentService.addNewGarment(garment);

        // Verificar el resultado
        assertNotNull(addedGarment);
        assertEquals(1L, addedGarment.getId().longValue());
    }

    @Test
    public void testGetAllGarmentsCreatedByUser() {

        // Crear una lista de garments de ejemplo
        List<Garment> garments = new ArrayList<>();
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setName("Garment 1");
        garment1.setGarmentState(GarmentState.CREATED);
        garment1.setUser(user);
        garments.add(garment1);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setName("Garment 2");
        garment2.setGarmentState(GarmentState.DELETED);
        garment2.setUser(user);
        garments.add(garment2);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(garments);

        // Llamar al método del servicio que quieres probar
        List<Garment> createdGarments = garmentService.getAllGarmentsCreatedByUser(user);

        // Verificar el resultado
        assertNotNull(createdGarments);
        assertEquals(1, createdGarments.size());
        assertEquals("Garment 1", createdGarments.get(0).getName());
        assertEquals(GarmentState.CREATED, createdGarments.get(0).getGarmentState());
    }

    @Test
    public void testGetAllGarmentsDeletedByUser() {

        // Crear una lista de garments de ejemplo
        List<Garment> garments = new ArrayList<>();
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setName("Garment 1");
        garment1.setGarmentState(GarmentState.CREATED);
        garment1.setUser(user);
        garments.add(garment1);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setName("Garment 2");
        garment2.setGarmentState(GarmentState.DELETED);
        garment2.setUser(user);
        garments.add(garment2);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(garments);

        // Llamar al método del servicio que quieres probar
        List<Garment> createdGarments = garmentService.getAllGarmentsDeletedByUser(user);

        // Verificar el resultado
        assertNotNull(createdGarments);
        assertEquals(1, createdGarments.size());
        assertEquals("Garment 2", createdGarments.get(0).getName());
        assertEquals(GarmentState.DELETED, createdGarments.get(0).getGarmentState());
    }

    @Test
    public void testGetAllGarmentsByUser() {

        // Crear una lista de garments de ejemplo
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setUser(user);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setUser(user);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garment1, garment2));

        // Llamar al método del servicio que quieres probar
        List<Garment> garments = garmentService.getAllGarmentsByUser(user);

        // Verificar el resultado
        assertNotNull(garments);
        assertEquals(2, garments.size());
        assertEquals(garment1.getId(), garments.get(0).getId());
        assertEquals(garment2.getId(), garments.get(1).getId());
    }


    @Test
    public void testGetGarmentByIdAndUser_GarmentFound() throws GarmentException {
        // Crear una lista de garments de ejemplo
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setUser(user);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setUser(user);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garment1, garment2));

        // Llamar al método del servicio que quieres probar
        Garment foundGarment = garmentService.getGarmentByIdAndUser(1L, user);

        // Verificar el resultado
        assertNotNull(foundGarment);
        assertEquals(1L, foundGarment.getId().longValue());
    }

    @Test
    public void testGetGarmentByIdAndUser_GarmentNotFound(){
        // Crear una lista de garments de ejemplo
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setUser(user);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setUser(user);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garment1, garment2));

        // Llamar al método del servicio que quieres probar y esperar una excepción
        try{
            garmentService.getGarmentByIdAndUser(3L, user);
            fail("Expected a GarmentException to be thrown");
        } catch (GarmentException e) {
            assertEquals(GarmentException.GARMENT_NOT_FOUND + user.getEmail(), e.getMessage());
        }

    }

    @Test
    public void testDeleteGarmentByUser_GarmentFound() throws GarmentException {

        // Crear una lista de garments de ejemplo
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setUser(user);
        garment1.setGarmentState(GarmentState.CREATED);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setUser(user);
        garment2.setGarmentState(GarmentState.CREATED);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garment1, garment2));
        when(garmentRepositoryMock.save(any(Garment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Llamar al método del servicio que quieres probar
        Garment deletedGarment = garmentService.deleteGarmentByUser(1L, user);

        // Verificar el resultado
        assertNotNull(deletedGarment);
        assertEquals(GarmentState.DELETED, deletedGarment.getGarmentState());
        assertNotNull(deletedGarment.getDeletedAt());
    }

    @Test
    public void testDeleteGarmentByUser_GarmentNotFound() {

        // Crear una lista de garments de ejemplo
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setUser(user);
        garment1.setGarmentState(GarmentState.CREATED);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setUser(user);
        garment2.setGarmentState(GarmentState.CREATED);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garment1, garment2));

        // Llamar al método del servicio que quieres probar y esperar una excepción
        try {
            garmentService.deleteGarmentByUser(3L, user);
            fail("Expected a GarmentException to be thrown");
        } catch (GarmentException e) {
            assertEquals(GarmentException.GARMENT_NOT_FOUND + user.getEmail(), e.getMessage());
        }
    }

    @Test
    public void testRestoreGarment_Success() throws GarmentException {

        Garment garmentToRestore = new Garment();
        garmentToRestore.setId(1L);
        garmentToRestore.setUser(user);
        garmentToRestore.setGarmentState(GarmentState.DELETED);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garmentToRestore));
        when(garmentRepositoryMock.save(any(Garment.class))).thenReturn(garmentToRestore);

        // Llamar al método del servicio que quieres probar
        Garment restoredGarment = garmentService.restoreGarment(1L, user);

        // Verificar el resultado
        assertEquals(GarmentState.CREATED, restoredGarment.getGarmentState());
        assertNull(restoredGarment.getDeletedAt());
        verify(garmentRepositoryMock, times(1)).save(garmentToRestore);
    }

    @Test
    public void testRestoreGarment_GarmentNotFound() {

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList());

        // Llamar al método del servicio que quieres probar y esperar una excepción
        try {
            garmentService.restoreGarment(1L, user);
            fail("Expected GarmentException to be thrown");
        } catch (GarmentException e) {
            assertEquals(GarmentException.GARMENT_NOT_FOUND + user.getEmail(), e.getMessage());
        }
    }

    @Test
    public void testRestoreGarment_AlreadyCreated() {
        Garment garmentToRestore = new Garment();
        garmentToRestore.setId(1L);
        garmentToRestore.setUser(user);
        garmentToRestore.setGarmentState(GarmentState.CREATED);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garmentToRestore));

        // Llamar al método del servicio que quieres probar y esperar una excepción
        try {
            garmentService.restoreGarment(1L, user);
            fail("Expected GarmentException to be thrown");
        } catch (GarmentException e) {
            assertEquals(GarmentException.GARMENT_NOT_FOUND + user.getEmail(), e.getMessage());
        }
    }

    @Test
    public void testGetAllGarmentsByCategory() {

        // Crear una lista de garments de ejemplo
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setUser(user);
        garment1.setCategory(GarmentCategory.TOP);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setUser(user);
        garment2.setCategory(GarmentCategory.BOTTOM);

        Garment garment3 = new Garment();
        garment3.setId(3L);
        garment3.setUser(user);
        garment3.setCategory(GarmentCategory.SHOES);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Arrays.asList(garment1, garment2, garment3));

        // Llamar al método del servicio que quieres probar
        List<Garment> tops = garmentService.getAllGarmentsByCategory(GarmentCategory.TOP, user);
        List<Garment> bottoms = garmentService.getAllGarmentsByCategory(GarmentCategory.BOTTOM, user);
        List<Garment> shoes = garmentService.getAllGarmentsByCategory(GarmentCategory.SHOES, user);

        // Verificar el resultado
        assertNotNull(tops);
        assertEquals(1, tops.size());
        assertEquals(garment1.getId(), tops.get(0).getId());
        assertEquals(GarmentCategory.TOP, tops.get(0).getCategory());

        assertNotNull(bottoms);
        assertEquals(1, bottoms.size());
        assertEquals(garment2.getId(), bottoms.get(0).getId());
        assertEquals(GarmentCategory.BOTTOM, bottoms.get(0).getCategory());

        assertNotNull(shoes);
        assertEquals(1, shoes.size());
        assertEquals(garment3.getId(), shoes.get(0).getId());
        assertEquals(GarmentCategory.SHOES, shoes.get(0).getCategory());
    }

    @Test
    public void testUpdateGarmentByUser_Success() {

        // Crear un garment de ejemplo
        Garment existingGarment = new Garment();
        existingGarment.setId(1L);
        existingGarment.setName("Existing Garment");
        existingGarment.setUser(user);

        // Crear el garment de actualización
        Garment updatedGarment = new Garment();
        updatedGarment.setId(1L);
        updatedGarment.setName("Updated Garment");
        updatedGarment.setUser(user);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.save(existingGarment)).thenReturn(updatedGarment);
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Collections.singletonList(existingGarment));

        // Llamar al método del servicio que quieres probar
        Garment result = garmentService.updateGarmentByUser(updatedGarment, user);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals("Updated Garment", result.getName());
        verify(garmentRepositoryMock, times(1)).save(existingGarment);
    }

    @Test
    public void testUpdateGarmentByUser_GarmentNotFound() {

        // Crear el garment de actualización
        Garment updatedGarment = new Garment();
        updatedGarment.setId(1L);
        updatedGarment.setName("Updated Garment");
        updatedGarment.setUser(user);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Collections.emptyList());

        // Llamar al método del servicio que quieres probar y esperar una excepción
        try {
            garmentService.updateGarmentByUser(updatedGarment, user);
            fail("Expected NoSuchElementException to be thrown");
        } catch (NoSuchElementException e) {
            assertEquals("Garment with id 1 not found for user test@example.com", e.getMessage());
        }

        // Verificar que no se intentó guardar ningún garment en el repositorio
        verify(garmentRepositoryMock, never()).save(any());
    }



    @Test
    public void testCleanTrash() {
        // Crear una lista de garments de ejemplo
        List<Garment> deletedGarments = new ArrayList<>();
        Garment garment1 = new Garment();
        garment1.setId(1L);
        garment1.setName("Garment 1");
        garment1.setGarmentState(GarmentState.DELETED);
        garment1.setImageName("image1.jpg");
        deletedGarments.add(garment1);

        Garment garment2 = new Garment();
        garment2.setId(2L);
        garment2.setName("Garment 2");
        garment2.setGarmentState(GarmentState.DELETED);
        garment2.setImageName("image2.jpg");
        deletedGarments.add(garment2);

        // Simular el comportamiento del repositorio y el servicio de almacenamiento
        when(garmentRepositoryMock.findByUser(any(User.class))).thenReturn(deletedGarments);

        // Llamar al método del servicio que quieres probar
        garmentService.cleanTrash(new User());

        // Verificar que se hayan eliminado las imágenes y los garments
        verify(storageServiceMock, times(1)).deleteImage("image1.jpg");
        verify(storageServiceMock, times(1)).deleteImage("image2.jpg");
        verify(garmentRepositoryMock, times(1)).delete(garment1);
        verify(garmentRepositoryMock, times(1)).delete(garment2);
    }

    @Test
    public void testDeleteGarmentFromTrash_Success() throws GarmentException {

        // Crear un garment de ejemplo en la papelera
        Garment garment = new Garment();
        garment.setId(1L);
        garment.setName("Garment");
        garment.setGarmentState(GarmentState.DELETED);
        garment.setUser(user);
        garment.setImageName("image.jpg");

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Collections.singletonList(garment));

        // Llamar al método del servicio que quieres probar
        garmentService.deleteGarmentFromTrash(1L, user);

        // Verificar que se eliminó la imagen y el garment en el repositorio
        verify(storageServiceMock, times(1)).deleteImage(anyString());
        verify(garmentRepositoryMock, times(1)).delete(any());
    }

    @Test
    public void testDeleteGarmentFromTrash_GarmentNotInTrash() {

        // Crear un garment de ejemplo que no esté en la papelera
        Garment garment = new Garment();
        garment.setId(1L);
        garment.setName("Garment");
        garment.setGarmentState(GarmentState.CREATED);
        garment.setUser(user);

        // Simular el comportamiento del repositorio
        when(garmentRepositoryMock.findByUser(user)).thenReturn(Collections.singletonList(garment));

        // Llamar al método del servicio que quieres probar y esperar una excepción
        try {
            garmentService.deleteGarmentFromTrash(1L, user);
            fail("Expected GarmentException to be thrown");
        } catch (GarmentException e) {
            assertEquals(GarmentException.GARMENT_NOT_IN_TRASH, e.getMessage());
        }

        // Verificar que no se intentó eliminar ninguna imagen ni ningún garment en el repositorio
        verify(storageServiceMock, never()).deleteImage(anyString());
        verify(garmentRepositoryMock, never()).delete(any());
    }

}

