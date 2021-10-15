package com.arnoldgalovics.inventory.service.api;

import com.arnoldgalovics.inventory.service.api.model.ErrorResponse;
import com.arnoldgalovics.inventory.service.exception.ProductCreationException;
import com.arnoldgalovics.inventory.service.exception.ProductInventoryNotFoundException;
import com.arnoldgalovics.inventory.service.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({ProductInventoryNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ProductCreationException.class})
    public ResponseEntity<ErrorResponse> handleProductCreationException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
