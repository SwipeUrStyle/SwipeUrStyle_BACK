package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.Garment;
import com.swipeurstyle.jwt.backend.entity.GarmentCategory;
import com.swipeurstyle.jwt.backend.entity.Outfit;
import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.repository.OutfitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutfitServiceTest {

    @Mock
    private OutfitRepository outfitRepositoryMock;

    @Mock
    private GarmentService garmentServiceMock;

    @InjectMocks
    private OutfitService outfitService;

    private User user;

    private Garment garmentTop;

    private Garment garmentBottom;

    private Garment garmentShoes;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
    }

    @BeforeEach
    void garmentsSetUp(){
        //Garments del outfit 1
        garmentTop = new Garment();
        garmentTop.setId(1L);
        garmentTop.setUser(user);
        garmentTop.setCategory(GarmentCategory.TOP);

        garmentBottom = new Garment();
        garmentBottom.setId(2L);
        garmentBottom.setUser(user);
        garmentBottom.setCategory(GarmentCategory.BOTTOM);

        garmentShoes = new Garment();
        garmentShoes.setId(3L);
        garmentShoes.setUser(user);
        garmentShoes.setCategory(GarmentCategory.SHOES);
    }

    @Test
    void testGetAllOutfitsByUser() {


        //Garments del outfit 2
        Garment garmentTop2 = new Garment();
        garmentTop2.setId(21L);
        garmentTop2.setUser(user);
        garmentTop2.setCategory(GarmentCategory.TOP);

        Garment garmentBottom2 = new Garment();
        garmentBottom2.setId(22L);
        garmentBottom2.setUser(user);
        garmentBottom2.setCategory(GarmentCategory.BOTTOM);

        Garment garmentShoes2 = new Garment();
        garmentShoes2.setId(23L);
        garmentShoes2.setUser(user);
        garmentShoes2.setCategory(GarmentCategory.SHOES);

        // Crear una lista de outfits de ejemplo para el usuario
        List<Outfit> outfits = new ArrayList<>();
        Outfit outfit1 = new Outfit();
        outfit1.setId(1L);
        outfit1.setTop(garmentTop);
        outfit1.setBottom(garmentBottom);
        outfit1.setShoes(garmentShoes);
        outfit1.setUser(user);
        outfits.add(outfit1);

        Outfit outfit2 = new Outfit();
        outfit2.setId(2L);
        outfit2.setTop(garmentTop2);
        outfit2.setBottom(garmentBottom2);
        outfit2.setShoes(garmentShoes2);
        outfit2.setUser(user);
        outfits.add(outfit2);

        // Simular el comportamiento del repositorio
        when(outfitRepositoryMock.findByUser(user)).thenReturn(outfits);

        // Llamar al método del servicio que quieres probar
        List<Outfit> userOutfits = outfitService.getAllOutfitsByUser(user);

        // Verificar el resultado
        assertEquals(2, userOutfits.size());
        assertEquals(1L, userOutfits.get(0).getId());
        assertEquals(2L, userOutfits.get(1).getId());
        assertEquals(user, userOutfits.get(0).getUser());
        assertEquals(user, userOutfits.get(1).getUser());
    }

    @Test
    void testAddNewOutfit_Success() {
        // Crear una lista de garments de ejemplo
        List<Garment> garments = new ArrayList<>();
        garments.add(garmentTop);
        garments.add(garmentBottom);
        garments.add(garmentShoes);

        // Crear una fecha programada para el outfit
        LocalDate scheduledFor = LocalDate.now().plusDays(1);

        // Configurar el comportamiento del outfitRepositoryMock para que devuelva el outfit correctamente
        when(outfitRepositoryMock.save(any(Outfit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Llamar al método del servicio que quieres probar
        Outfit outfit = outfitService.addNewOutfit(garments, scheduledFor, user, true);

        // Verificar el resultado
        assertEquals(garmentTop, outfit.getTop());
        assertEquals(garmentBottom, outfit.getBottom());
        assertEquals(garmentShoes, outfit.getShoes());
        assertEquals(user, outfit.getUser());
        assertEquals(scheduledFor, outfit.getScheduledFor());
        assertEquals(LocalDate.now(), outfit.getCreatedAt());
    }

    @Test
    void testAddNewOutfit_IncorrectNumberOfGarments() {
        // Crear una lista de garments de ejemplo con un número incorrecto de garments
        List<Garment> garments = new ArrayList<>();

        garments.add(garmentTop);
        garments.add(garmentBottom);

        // Crear una fecha programada para el outfit
        LocalDate scheduledFor = LocalDate.now().plusDays(1);

        // Verificar que se lance una excepción al intentar agregar un outfit con un número incorrecto de garments
        try {
            outfitService.addNewOutfit(garments, scheduledFor, user, true);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e){
            assertEquals("The outfit must have three items", e.getMessage());
        }

    }

    @Test
    void testAddNewOutfit_OutfitItemsCannotBeNull() {
        // Crear una lista de garments de ejemplo con un número incorrecto de garments
        List<Garment> garments = new ArrayList<>();

        garments.add(garmentTop);
        garments.add(garmentBottom);
        garments.add(garmentBottom);

        // Crear una fecha programada para el outfit
        LocalDate scheduledFor = LocalDate.now().plusDays(1);

        // Verificar que se lance una excepción al intentar agregar un outfit con un número incorrecto de garments
        try {
            outfitService.addNewOutfit(garments, scheduledFor, user, true);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e){
            assertEquals("All outfit's items must not be null", e.getMessage());
        }

    }

    @Test
    void testGetAllScheduledOutfits() {
        // Crear una lista de outfits de ejemplo, algunos programados y otros no
        List<Outfit> outfits = new ArrayList<>();
        Outfit scheduledOutfit = new Outfit();
        scheduledOutfit.setId(1L);
        scheduledOutfit.setUser(user);
        scheduledOutfit.setScheduled(true);
        outfits.add(scheduledOutfit);

        Outfit unscheduledOutfit = new Outfit();
        unscheduledOutfit.setId(2L);
        unscheduledOutfit.setUser(user);
        unscheduledOutfit.setScheduled(false);
        outfits.add(unscheduledOutfit);

        // Configurar el comportamiento del outfitRepositoryMock para devolver los outfits
        when(outfitRepositoryMock.findByUser(user)).thenReturn(outfits);

        // Llamar al método del servicio que quieres probar
        List<Outfit> scheduledOutfits = outfitService.getAllScheduledOutfits(user);

        // Verificar el resultado
        assertNotNull(scheduledOutfits);
        assertEquals(1, scheduledOutfits.size());
        assertEquals(1L, scheduledOutfits.get(0).getId().longValue());
    }

    @Test
    void testGetAllNotScheduledOutfits() {
        // Crear una lista de outfits de ejemplo, algunos programados y otros no
        List<Outfit> outfits = new ArrayList<>();
        Outfit scheduledOutfit = new Outfit();
        scheduledOutfit.setId(1L);
        scheduledOutfit.setUser(user);
        scheduledOutfit.setScheduled(true);
        outfits.add(scheduledOutfit);

        Outfit unscheduledOutfit = new Outfit();
        unscheduledOutfit.setId(2L);
        unscheduledOutfit.setUser(user);
        unscheduledOutfit.setScheduled(false);
        outfits.add(unscheduledOutfit);

        // Configurar el comportamiento del outfitRepositoryMock para devolver los outfits
        when(outfitRepositoryMock.findByUser(user)).thenReturn(outfits);

        // Llamar al método del servicio que quieres probar
        List<Outfit> notScheduledOutfits = outfitService.getAllNotScheduledOutfits(user);

        // Verificar el resultado
        assertNotNull(notScheduledOutfits);
        assertEquals(1, notScheduledOutfits.size());
        assertEquals(2L, notScheduledOutfits.get(0).getId().longValue());
    }

    @Test
    void testGetAllFavoriteOutfits() {
        // Crear una lista de outfits de ejemplo, algunos marcados como favoritos y otros no
        List<Outfit> outfits = new ArrayList<>();
        Outfit favoriteOutfit = new Outfit();
        favoriteOutfit.setId(1L);
        favoriteOutfit.setUser(user);
        favoriteOutfit.setFavorite(true);
        outfits.add(favoriteOutfit);

        Outfit notFavoriteOutfit = new Outfit();
        notFavoriteOutfit.setId(2L);
        notFavoriteOutfit.setUser(user);
        notFavoriteOutfit.setFavorite(false);
        outfits.add(notFavoriteOutfit);

        // Configurar el comportamiento del outfitRepositoryMock para devolver los outfits
        when(outfitRepositoryMock.findByUser(user)).thenReturn(outfits);

        // Llamar al método del servicio que quieres probar
        List<Outfit> favoriteOutfits = outfitService.getAllFavoriteOutfits(user);

        // Verificar el resultado
        assertNotNull(favoriteOutfits);
        assertEquals(1, favoriteOutfits.size());
        assertEquals(1L, favoriteOutfits.get(0).getId().longValue());
    }

    @Test
    void testDeleteOutfitByUser_Success() {
        // Crear un outfit de ejemplo y configurar el comportamiento del outfitRepositoryMock
        Outfit outfitToDelete = new Outfit();
        outfitToDelete.setId(1L);
        outfitToDelete.setUser(user);
        when(outfitRepositoryMock.findById(1L)).thenReturn(Optional.of(outfitToDelete));

        // Llamar al método del servicio que quieres probar
        Outfit deletedOutfit = outfitService.deleteOutfitByUser(user, 1L);

        // Verificar que el outfit se haya eliminado correctamente y devuelto
        assertNotNull(deletedOutfit);
        assertEquals(1L, deletedOutfit.getId().longValue());
        verify(outfitRepositoryMock, times(1)).delete(outfitToDelete);
    }

    @Test
    void testDeleteOutfitByUser_OutfitNotFound() {
        // Configurar el comportamiento del outfitRepositoryMock para devolver un Optional.empty()
        when(outfitRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        // Llamar al método del servicio que quieres probar
        Outfit deletedOutfit = outfitService.deleteOutfitByUser(user, 1L);

        // Verificar que se devuelva null si el outfit no se encuentra
        assertNull(deletedOutfit);
    }

    @Test
    void testDeleteOutfitByUser_OutfitNotOwnedByUser() {
        // Crear un outfit de ejemplo que no pertenezca al usuario
        Outfit outfitToDelete = new Outfit();
        outfitToDelete.setId(1L);
        outfitToDelete.setUser(new User()); // Un usuario diferente al usuario actual
        when(outfitRepositoryMock.findById(1L)).thenReturn(Optional.of(outfitToDelete));

        // Llamar al método del servicio que quieres probar
        Outfit deletedOutfit = outfitService.deleteOutfitByUser(user, 1L);

        // Verificar que se devuelva null si el outfit no pertenece al usuario
        assertNull(deletedOutfit);
    }


    @Test
    void testGetOutfitByIdAndUser_OutfitFound() {
        // Crear una lista de outfits de ejemplo para el usuario
        List<Outfit> outfits = new ArrayList<>();
        Outfit outfit1 = new Outfit();
        outfit1.setId(1L);
        outfits.add(outfit1);
        Outfit outfit2 = new Outfit();
        outfit2.setId(2L);
        outfits.add(outfit2);
        when(outfitRepositoryMock.findByUser(user)).thenReturn(outfits);

        // Llamar al método del servicio que quieres probar
        Outfit foundOutfit = outfitService.getOutfitByIdAndUser(1L, user);

        // Verificar que el outfit correcto se devuelva
        assertEquals(outfit1, foundOutfit);
    }

    @Test
    void testGetOutfitByIdAndUser_OutfitNotFound() {
        // Configurar el comportamiento del outfitRepositoryMock para devolver una lista vacía de outfits
        when(outfitRepositoryMock.findByUser(user)).thenReturn(Collections.emptyList());

        // Verificar que se lance una excepción si el outfit no se encuentra
        try {
            outfitService.getOutfitByIdAndUser(1L, user);
            fail("Expected NoSuchElementException to be thrown");
        } catch (NoSuchElementException e){
            assertEquals("Outfit with id " + 1L + " not found for user " + user.getEmail(), e.getMessage());
        }
    }

    @Test
    void testUpdateOutfitByUser_OutfitFound() {
        // Crear una lista de outfits de ejemplo para el usuario
        List<Outfit> outfits = new ArrayList<>();
        Outfit outfitToUpdate = new Outfit();
        outfitToUpdate.setId(1L);
        outfitToUpdate.setUser(user);
        outfits.add(outfitToUpdate);

        when(outfitRepositoryMock.findByUser(user)).thenReturn(outfits);
        when(outfitRepositoryMock.save(any(Outfit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Llamar al método del servicio que quieres probar
        Outfit updatedOutfit = outfitService.updateOutfitByUser(outfitToUpdate, user);

        // Verificar que el outfit se actualice correctamente
        assertEquals(1L, updatedOutfit.getId());
    }

    @Test
    void testUpdateOutfitByUser_OutfitNotFound() {
        Outfit outfit = new Outfit();
        outfit.setId(1L);
        // Configurar el comportamiento del outfitRepositoryMock para devolver una lista vacía de outfits
        when(outfitRepositoryMock.findByUser(user)).thenReturn(Collections.emptyList());

        // Verificar que se lance una excepción si el outfit no se encuentra
        try {
            outfitService.updateOutfitByUser(outfit, user);
            fail("Expected NoSuchElementException to be thrown");
        } catch (NoSuchElementException e){
            assertEquals("Outfit with id " + 1L + " not found for user " + user.getEmail(), e.getMessage());
        }    }
}