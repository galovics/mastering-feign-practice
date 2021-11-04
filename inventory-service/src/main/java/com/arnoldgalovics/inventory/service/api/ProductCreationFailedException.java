package com.arnoldgalovics.inventory.service.api;

public class ProductCreationFailedException extends RuntimeException {
    public ProductCreationFailedException(String message) {
        super(message);
    }
}
