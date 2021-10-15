package com.arnoldgalovics.inventory.service.exception;

public class ProductCreationException extends RuntimeException {
    public ProductCreationException(String msg) {
        super(msg);
    }
}
