package com.arnoldgalovics.inventory.service.api;

public class ProductCreationFailedException extends RuntimeException {
    public ProductCreationFailedException(String msg) {
        super(msg);
    }
}
