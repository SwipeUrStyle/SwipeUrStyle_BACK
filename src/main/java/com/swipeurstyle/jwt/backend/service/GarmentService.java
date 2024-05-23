package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.Garment;
import com.swipeurstyle.jwt.backend.entity.GarmentCategory;
import com.swipeurstyle.jwt.backend.entity.GarmentState;
import com.swipeurstyle.jwt.backend.entity.User;
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
            if (garment.getGarmentState().equals(GarmentState.CREATED)) {
                garmentCreated.add(garment);
            }
        }
        return garmentCreated;
    }

    public List<Garment> getAllGarmentsDeletedByUser(User user) {
        List<Garment> garments = garmentRepository.findByUser(user);
        List<Garment> garmentDeleted = new ArrayList<>();
        for (Garment garment : garments) {
            if (garment.getGarmentState().equals(GarmentState.DELETED)) {
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
            if (garment.getId().equals(garmentId)) {
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
            if (garment.getId().equals(garmentId)) {
                garmentToDelete = garment;
                break;
            }
        }
        if (garmentToDelete == null) {
            throw new GarmentException(GarmentException.GARMENT_NOT_FOUND + user.getEmail());
        }

        // Eliminar todos los conjuntos que contienen esta prenda
        outfitRepository.deleteByTop(garmentToDelete);
        outfitRepository.deleteByBottom(garmentToDelete);
        outfitRepository.deleteByShoes(garmentToDelete);


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
        garmentRepository.deleteByDeletedAtBefore(thirtyDaysAgo);
    }

    public Garment restoreGarment(Long garmentId, User user) throws GarmentException {
        Garment garmentToRestore = getGarmentByIdAndUser(garmentId, user);
        if (garmentToRestore == null) {
            throw new GarmentException(GarmentException.GARMENT_NOT_FOUND + user.getEmail());
        }
        if (garmentToRestore.getGarmentState().equals(GarmentState.CREATED)) {
            throw new GarmentException(GarmentException.GARMENT_NOT_FOUND + user.getEmail());
        }
        garmentToRestore.setGarmentState(GarmentState.CREATED);
        garmentToRestore.setDeletedAt(null);

        return garmentRepository.save(garmentToRestore);
    }

    public List<Garment> getAllGarmentsByCategory(GarmentCategory category, User user) {
        List<Garment> garments = getAllGarmentsByUser(user);
        List<Garment> garmentsByCategory = new ArrayList<>();
        for (Garment garment : garments) {
            if (garment.getCategory().equals(category)) {
                garmentsByCategory.add(garment);
            }
        }
        return garmentsByCategory;
    }

    public Garment updateGarmentByUser(Garment update, User user) {
        List<Garment> garments = getAllGarmentsByUser(user);
        Garment garmentToUpdate = null;
        for (Garment garment : garments) {
            if (garment.getId().equals(update.getId())) {
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
            // Eliminar todos los conjuntos asociados
            outfitRepository.deleteByTop(garment);
            outfitRepository.deleteByBottom(garment);
            outfitRepository.deleteByShoes(garment);

            String imageName = garment.getImageName();
            storageService.deleteImage(imageName);
            garmentRepository.delete(garment);
        }
    }

    public void deleteGarmentFromTrash(Long garmentId, User user) throws GarmentException {
        List<Garment> garments = getAllGarmentsDeletedByUser(user);
        Garment garmentToDelete = null;
        for (Garment garment : garments) {
            if (garment.getId().equals(garmentId)) {
                garmentToDelete = garment;
                break;
            }
        }
        if (garmentToDelete == null) {
            throw new GarmentException(GarmentException.GARMENT_NOT_IN_TRASH);
        }

        String imageName = garmentToDelete.getImageName();
        storageService.deleteImage(imageName);
        garmentRepository.delete(garmentToDelete);
    }

}
