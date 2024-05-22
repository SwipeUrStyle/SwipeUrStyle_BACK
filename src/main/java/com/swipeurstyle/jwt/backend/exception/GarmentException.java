package com.swipeurstyle.jwt.backend.exception;

public class GarmentException extends Exception {
    public static final String GARMENT_NOT_FOUND = "Garment not found for user ";

    public GarmentException(String message) {
        super(message);
    }
}
