package com.arnoldgalovics.inventory.service.exception;

public class ProductInventoryNotFoundException extends RuntimeException {
    public ProductInventoryNotFoundException(String msg) {
        super(msg);
    }
}
