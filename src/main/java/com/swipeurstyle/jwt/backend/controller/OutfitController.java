package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.*;
import com.swipeurstyle.jwt.backend.repository.SessionRepository;
import com.swipeurstyle.jwt.backend.service.GarmentService;
import com.swipeurstyle.jwt.backend.service.OutfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class OutfitController {
    private final OutfitService outfitService;
    private final SessionRepository sessionRepository;

    private final GarmentService garmentService;

    @Autowired
    public OutfitController(OutfitService outfitService,
                             SessionRepository sessionRepository,
                            GarmentService garmentService) {
        this.outfitService = outfitService;
        this.sessionRepository = sessionRepository;
        this.garmentService = garmentService;
    }

    @PostMapping(value = {"/outfit"})
    public ResponseEntity<Outfit> addNewOutfit(@RequestBody OutfitRequest outfitRequest,
                                               @CookieValue(name = "authToken") String authToken){
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        List<Garment> garments = new ArrayList<>();
        Garment top = garmentService.getGarmentByIdAndUser(outfitRequest.getTopId(), user);
        garments.add(top);
        Garment bottom = garmentService.getGarmentByIdAndUser(outfitRequest.getBottomId(), user);
        garments.add(bottom);
        Garment shoes = garmentService.getGarmentByIdAndUser(outfitRequest.getShoesId(), user);
        garments.add(shoes);

        boolean scheduled = outfitRequest.isScheduled();

        LocalDateTime scheduledFor = outfitRequest.getScheduledFor();

        return new ResponseEntity<>(outfitService.addNewOutfit(garments, scheduledFor, user, scheduled), HttpStatus.CREATED);
    }



    @GetMapping({"/outfits"})
    public ResponseEntity<List<Outfit>> getAllOutfits(@CookieValue(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(outfitService.getAllOutfitsByUser(user), HttpStatus.FOUND);
    }

    @GetMapping({"/outfits/scheduled"})
    public ResponseEntity<List<Outfit>> getAllOutfitsScheduled(@CookieValue(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(outfitService.getAllScheduledOutfits(user), HttpStatus.FOUND);
    }

    @GetMapping({"/outfits/not-scheduled"})
    public ResponseEntity<List<Outfit>> getAllOutfitsNotScheduled(@CookieValue(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(outfitService.getAllNotScheduledOutfits(user), HttpStatus.FOUND);
    }

    @DeleteMapping("/outfit/{id}")
    public ResponseEntity<Outfit> deleteOutfit(@PathVariable Long id, @CookieValue(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = session.getUser();

        Optional<Outfit> optionalOutfit = Optional.ofNullable(outfitService.getOutfitByIdAndUser(id, user));
        if (!optionalOutfit.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Outfit outfitToDelete = optionalOutfit.get();

        outfitService.deleteOutfitByUser(user, outfitToDelete.getId());

        return new ResponseEntity<>(outfitToDelete,HttpStatus.OK);
    }

    @PatchMapping("/outfit/{id}")
    public ResponseEntity<Outfit> partialUpdateOutfit(
            @PathVariable("id") Long outfitId,
            @RequestBody Map<String, Object> updates,
            @CookieValue(name = "authToken") String authToken
    ){
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = session.getUser();
        Outfit outfit = outfitService.getOutfitByIdAndUser(outfitId, user);
        if (outfit == null) {
            return ResponseEntity.notFound().build();
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "top":
                    Garment top = garmentService.getGarmentByIdAndUser((Long) value, user);
                    outfit.setTop(top);
                    break;
                case "bottom":
                    Garment bottom = garmentService.getGarmentByIdAndUser((Long) value, user);
                    outfit.setBottom(bottom);
                    break;
                case "shoes":
                    Garment shoes = garmentService.getGarmentByIdAndUser((Long) value, user);
                    outfit.setShoes(shoes);
                    break;
                case "scheduled":
                    boolean scheduled = (boolean) value;
                    outfit.setScheduled(scheduled);
                    break;
                case "scheduledFor":
                    LocalDateTime scheduledFor = (LocalDateTime) value;
                    outfit.setScheduledFor(scheduledFor);
                    break;
            }
        });
        Outfit updatedOutfit = outfitService.updateOutfitByUser(outfit, user);
        return ResponseEntity.ok(updatedOutfit);
    }
}
