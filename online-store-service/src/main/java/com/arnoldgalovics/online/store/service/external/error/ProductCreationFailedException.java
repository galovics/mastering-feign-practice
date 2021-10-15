package com.arnoldgalovics.online.store.service.external.error;

public class ProductCreationFailedException extends RuntimeException {
    public ProductCreationFailedException(String msg) {
        super(msg);
    }
}
