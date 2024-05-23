package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.*;
import com.swipeurstyle.jwt.backend.exception.GarmentException;
import com.swipeurstyle.jwt.backend.repository.GarmentRepository;
import com.swipeurstyle.jwt.backend.repository.OutfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GarmentService {

    private final GarmentRepository garmentRepository;
    private final StorageService storageService;

    private final OutfitRepository outfitRepository;

    @Autowired
    public GarmentService(GarmentRepository garmentRepository, StorageService storageService, OutfitRepository outfitRepository) {
        this.garmentRepository = garmentRepository;
        this.storageService = storageService;
        this.outfitRepository = outfitRepository;
    }

    public Garment addNewGarment(Garment garment) {
        return garmentRepository.save(garment);
    }


    public List<Garment> getAllGarmentsCreatedByUser(User user) {
        List<Garment> garments = getAllGarmentsByUser(user);
        List<Garment> garmentCreated = new ArrayList<>();
        for (Garment garment : garments) {
            if (garment.getGarmentState().equals(GarmentState.CREATED) && garment.isEnabled()) {
                garmentCreated.add(garment);
            }
        }
        return garmentCreated;
    }

    public List<Garment> getAllGarmentsDeletedByUser(User user) {
        List<Garment> garments = garmentRepository.findByUser(user);
        List<Garment> garmentDeleted = new ArrayList<>();
        for (Garment garment : garments) {
            if (garment.getGarmentState().equals(GarmentState.DELETED) && garment.isEnabled()) {
                garmentDeleted.add(garment);
            }
        }
        return garmentDeleted;
    }

    public List<Garment> getAllGarmentsByUser(User user) {
        return garmentRepository.findByUser(user);
    }


    public Garment getGarmentByIdAndUser(Long garmentId, User user) throws GarmentException {
        List<Garment> garments = getAllGarmentsByUser(user);
        Garment garmentToFind = null;
        for (Garment garment : garments) {
            if (garment.getId().equals(garmentId) && garment.isEnabled()) {
                garmentToFind = garment;
                break;
            }
        }
        if (garmentToFind == null) {
            throw new GarmentException(GarmentException.GARMENT_NOT_FOUND + user.getEmail());
        }

        return garmentToFind;
    }

    public Garment deleteGarmentByUser(Long garmentId, User user) throws GarmentException {
        List<Garment> garments = getAllGarmentsCreatedByUser(user);
        Garment garmentToDelete = null;
        for (Garment garment : garments) {
            if (garment.getId().equals(garmentId) && garment.isEnabled()) {
                garmentToDelete = garment;
                break;
            }
        }
        if (garmentToDelete == null) {
            throw new GarmentException(GarmentException.GARMENT_NOT_FOUND + user.getEmail());
        }

        // Eliminar todos los conjuntos que contienen esta prenda
        deleteOutfitsAssociated(garmentToDelete);

        garmentToDelete.setGarmentState(GarmentState.DELETED);
        garmentToDelete.setDeletedAt(LocalDateTime.now());


        return garmentRepository.save(garmentToDelete);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Se ejecuta a la medianoche todos los días
    public void cleanGarmentDeletedDaily() {
        cleanGarmentDeleted();
    }

    public void cleanGarmentDeleted() {
        // Lógica para limpiar la papelera temporal
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Garment> garments = garmentRepository.findByDeletedAtBefore(thirtyDaysAgo);
        for (Garment garment : garments) {
            deleteOutfitsAssociated(garment);
            garment.setEnabled(false);
            garmentRepository.save(garment);
        }
    }

    public Garment restoreGarment(Long garmentId, User user) throws GarmentException {
        Garment garmentToRestore = getGarmentByIdAndUser(garmentId, user);
        if (garmentToRestore == null) {
            throw new GarmentException(GarmentException.GARMENT_NOT_FOUND + user.getEmail());
        }
        if (garmentToRestore.getGarmentState().equals(GarmentState.CREATED)) {
            throw new GarmentException(GarmentException.GARMENT_NOT_FOUND + user.getEmail());
        }
        if (!garmentToRestore.isEnabled()){
            return null;
        }
        garmentToRestore.setGarmentState(GarmentState.CREATED);
        garmentToRestore.setDeletedAt(null);

        return garmentRepository.save(garmentToRestore);
    }

    public List<Garment> getAllGarmentsByCategory(GarmentCategory category, User user) {
        List<Garment> garments = getAllGarmentsByUser(user);
        List<Garment> garmentsByCategory = new ArrayList<>();
        for (Garment garment : garments) {
            if (garment.getCategory().equals(category) && garment.isEnabled()) {
                garmentsByCategory.add(garment);
            }
        }
        return garmentsByCategory;
    }

    public Garment updateGarmentByUser(Garment update, User user) {
        List<Garment> garments = getAllGarmentsByUser(user);
        Garment garmentToUpdate = null;
        for (Garment garment : garments) {
            if (garment.getId().equals(update.getId()) && garment.isEnabled()) {
                garmentToUpdate = garment;
                break;
            }
        }
        if (garmentToUpdate == null) {
            throw new NoSuchElementException("Garment with id " + update.getId() + " not found for user " + user.getEmail());
        }
        return garmentRepository.save(garmentToUpdate);
    }

    public void cleanTrash(User user) {
        List<Garment> deletedGarments = getAllGarmentsDeletedByUser(user);
        for (Garment garment : deletedGarments) {
            deleteOutfitsAssociated(garment);

            String imageName = garment.getImageName();
            storageService.deleteImage(imageName);
            garment.setEnabled(false);
            garmentRepository.save(garment);
        }
    }

    public void deleteGarmentFromTrash(Long garmentId, User user) throws GarmentException {
        List<Garment> garments = getAllGarmentsDeletedByUser(user);
        Garment garmentToDelete = null;
        for (Garment garment : garments) {
            if (garment.getId().equals(garmentId) && garment.isEnabled()) {
                garmentToDelete = garment;
                break;
            }
        }
        if (garmentToDelete == null) {
            throw new GarmentException(GarmentException.GARMENT_NOT_IN_TRASH);
        }

        deleteOutfitsAssociated(garmentToDelete);

        String imageName = garmentToDelete.getImageName();
        storageService.deleteImage(imageName);
        garmentToDelete.setEnabled(false);
        garmentRepository.save(garmentToDelete);
    }

    private void deleteOutfitsAssociated(Garment garment) {
        if (garment.getCategory() == null) {
            return; // No hacer nada si la categoría es null
        }
        switch (garment.getCategory()) {
            case TOP:
                List<Outfit> outfitsTops = outfitRepository.findByTop(garment);
                for (Outfit outfit: outfitsTops) {
                    outfit.setEnabled(false);
                    outfitRepository.save(outfit);
                }
                break;
            case BOTTOM:
                List<Outfit> outfitsBottoms = outfitRepository.findByBottom(garment);
                for (Outfit outfit: outfitsBottoms) {
                    outfit.setEnabled(false);
                    outfitRepository.save(outfit);
                }
                break;
            case SHOES:
                List<Outfit> outfitsShoes = outfitRepository.findByShoes(garment);
                for (Outfit outfit: outfitsShoes) {
                    outfit.setEnabled(false);
                    outfitRepository.save(outfit);
                }
                break;
        }
    }


}
