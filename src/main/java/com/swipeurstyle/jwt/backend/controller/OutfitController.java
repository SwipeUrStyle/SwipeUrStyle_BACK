package com.swipeurstyle.jwt.backend.controller;

import com.swipeurstyle.jwt.backend.entity.*;
import com.swipeurstyle.jwt.backend.exception.GarmentException;
import com.swipeurstyle.jwt.backend.repository.SessionRepository;
import com.swipeurstyle.jwt.backend.service.GarmentService;
import com.swipeurstyle.jwt.backend.service.OutfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
                                               @RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        List<Garment> garments = new ArrayList<>();
        try {
            Garment top = garmentService.getGarmentByIdAndUser(outfitRequest.getTopId(), user);
            garments.add(top);
            Garment bottom = garmentService.getGarmentByIdAndUser(outfitRequest.getBottomId(), user);
            garments.add(bottom);
            Garment shoes = garmentService.getGarmentByIdAndUser(outfitRequest.getShoesId(), user);
            garments.add(shoes);
        } catch (GarmentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        boolean scheduled = outfitRequest.isScheduled();

        LocalDate scheduledFor = outfitRequest.getScheduledFor();

        return new ResponseEntity<>(outfitService.addNewOutfit(garments, scheduledFor, user, scheduled), HttpStatus.CREATED);
    }


    @GetMapping({"/outfits"})
    public ResponseEntity<List<Outfit>> getAllOutfits(@RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(outfitService.getAllOutfitsByUser(user), HttpStatus.FOUND);
    }

    @GetMapping({"/outfits/scheduled"})
    public ResponseEntity<List<Outfit>> getAllOutfitsScheduled(@RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(outfitService.getAllScheduledOutfits(user), HttpStatus.FOUND);
    }

    @GetMapping({"/outfits/not-scheduled"})
    public ResponseEntity<List<Outfit>> getAllOutfitsNotScheduled(@RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(outfitService.getAllNotScheduledOutfits(user), HttpStatus.FOUND);
    }

    @GetMapping({"/outfits/favorites"})
    public ResponseEntity<List<Outfit>> getAllOFavoriteOutfits(@RequestHeader(name = "authToken") String authToken) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User user = session.getUser();
        return new ResponseEntity<>(outfitService.getAllFavoriteOutfits(user), HttpStatus.FOUND);
    }

    @DeleteMapping("/outfit/{id}")
    public ResponseEntity<Outfit> deleteOutfit(@PathVariable Long id, @RequestHeader(name = "authToken") String authToken) {
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

        Outfit deletedOutfit = outfitService.deleteOutfitByUser(user, outfitToDelete.getId());

        return new ResponseEntity<>(deletedOutfit, HttpStatus.OK);
    }

    @PatchMapping("/outfit/{id}")
    public ResponseEntity<Outfit> partialUpdateOutfit(
            @PathVariable("id") Long outfitId,
            @RequestBody Map<String, Object> updates,
            @RequestHeader(name = "authToken") String authToken
    ) {
        Session session = sessionRepository.findByToken(UUID.fromString(authToken));
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = session.getUser();
        Outfit outfit = outfitService.getOutfitByIdAndUser(outfitId, user);
        if (outfit == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                switch (key) {
                    case "top":
                        Garment top = garmentService.getGarmentByIdAndUser(Long.parseLong(value.toString()), user);
                        outfit.setTop(top);
                        break;
                    case "bottom":
                        Garment bottom = garmentService.getGarmentByIdAndUser(Long.parseLong(value.toString()), user);
                        outfit.setBottom(bottom);
                        break;
                    case "shoes":
                        Garment shoes = garmentService.getGarmentByIdAndUser(Long.parseLong(value.toString()), user);
                        outfit.setShoes(shoes);
                        break;
                    case "scheduled":
                        boolean scheduled = Boolean.parseBoolean(value.toString());
                        outfit.setScheduled(scheduled);
                        break;
                    case "scheduledFor":
                        LocalDate scheduledFor = LocalDate.parse(value.toString());
                        outfit.setScheduled(true);
                        outfit.setScheduledFor(scheduledFor);
                        break;
                    case "favorite":
                        boolean favorite = Boolean.parseBoolean(value.toString());
                        outfit.setFavorite(favorite);
                        break;
                    default:
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            Outfit updatedOutfit = outfitService.updateOutfitByUser(outfit, user);
            return ResponseEntity.ok(updatedOutfit);
        } catch (GarmentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
