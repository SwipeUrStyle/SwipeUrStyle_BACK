package com.swipeurstyle.jwt.backend.exception;

public class GarmentException extends Exception {
    public static final String GARMENT_NOT_FOUND = "Garment not found for user ";
    public static final String GARMENT_NOT_IN_TRASH = "Garment not in trash cannot be deleted";

    public GarmentException(String message) {
        super(message);
    }
}
