package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.*;
import com.swipeurstyle.jwt.backend.repository.OutfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OutfitService {

    private final OutfitRepository outfitRepository;

    private final GarmentService garmentService;

    @Autowired
    public OutfitService(OutfitRepository outfitRepository, GarmentService garmentService) {
        this.outfitRepository = outfitRepository;
        this.garmentService = garmentService;
    }

    public List<Outfit> getAllOutfitsByUser(User user){
        return outfitRepository.findByUser(user);
    }

    public Outfit addNewOutfit(List<Garment> garments, Optional<LocalDateTime> scheduledFor, User user, boolean scheduled) {
        if (garments.size() != 3) {
            throw new IllegalArgumentException("The outfit must have three items");
        }

        Garment top = null;
        Garment bottom = null;
        Garment shoes = null;

        for (Garment garment : garments) {
            if (garment.getUser().equals(user)) {
                if (garment.getCategory().equals(GarmentCategory.TOP)) {
                    top = garment;
                } else if (garment.getCategory().equals(GarmentCategory.BOTTOM)) {
                    bottom = garment;
                } else if (garment.getCategory().equals(GarmentCategory.SHOES)) {
                    shoes = garment;
                }
            }
        }

        if (top == null || bottom == null || shoes == null) {
            throw new IllegalArgumentException("All outfit's items must not be null");
        }

        Outfit outfit = new Outfit();
        outfit.setTop(top);
        outfit.setBottom(bottom);
        outfit.setShoes(shoes);
        outfit.setScheduled(scheduled);
        scheduledFor.ifPresent(outfit::setScheduledFor); // Set scheduledFor only if present
        outfit.setCreatedAt(LocalDateTime.now());
        outfit.setUser(user);

        return outfitRepository.save(outfit);
    }


    public Outfit updateOutfitToScheduled(LocalDateTime scheduledFor, Outfit outfit, User user) {
        if (outfitRepository.findById(outfit.getId()).isPresent()) {
            if(outfit.getUser().equals(user)) {
                outfit.setScheduled(true);
                outfit.setScheduledFor(scheduledFor);
                return outfitRepository.save(outfit);
            }
        }
        return null;
    }

    public List<Outfit> getAllScheduledOutfits(User user) {
        List<Outfit> outfits = getAllOutfitsByUser(user);
        List<Outfit> scheduledOutfits = new ArrayList<>();
        for (Outfit outfit : outfits) {
            if (outfit.isScheduled()){
                scheduledOutfits.add(outfit);
            }
        }
        return scheduledOutfits;
    }

    public List<Outfit> getAllNotScheduledOutfits(User user) {
        List<Outfit> outfits = getAllOutfitsByUser(user);
        List<Outfit> notScheduledOutfits = new ArrayList<>();
        for (Outfit outfit : outfits) {
            if (!outfit.isScheduled()){
                notScheduledOutfits.add(outfit);
            }
        }
        return notScheduledOutfits;
    }

    public Outfit deleteOutfitByUser(User user, Long id){
        Optional<Outfit> outfitToDelete = outfitRepository.findById(id);
        if (outfitToDelete.isPresent()){
            if (outfitToDelete.get().getUser().equals(user)){
                outfitRepository.delete(outfitToDelete.get());
                return outfitToDelete.get();
            }
        }
        return null;
    }

    public Outfit getOutfitByIdAndUser(Long outfitId, User user) {
        List<Outfit> outfits = getAllOutfitsByUser(user);
        Outfit outfitToFind = null;
        for (Outfit outfit : outfits) {
            if (outfit.getId().equals(outfitId)) {
                outfitToFind = outfit;
                break;
            }
        }
        if (outfitToFind == null) {
            throw new NoSuchElementException("Outfit with id " + outfitId + " not found for user " + user.getEmail());
        }

        return outfitToFind;
    }

    public Outfit updateOutfitByUser(Outfit update, User user) {
        List<Outfit> outfits = getAllOutfitsByUser(user);
        Outfit outfitToUpdate = null;
        for (Outfit outfit : outfits) {
            if (outfit.getId().equals(update.getId())) {
                outfitToUpdate = outfit;
                break;
            }
        }
        if (outfitToUpdate == null) {
            throw new NoSuchElementException("Outfit with id " + update.getId() + " not found for user " + user.getEmail());
        }
        return outfitRepository.save(outfitToUpdate);
    }
}
