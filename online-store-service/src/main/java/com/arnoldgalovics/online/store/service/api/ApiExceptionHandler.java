package com.arnoldgalovics.online.store.service.api;

import com.arnoldgalovics.online.store.service.external.inventory.ProductCreationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({ProductCreationFailedException.class})
    public ResponseEntity<ErrorResponse> handle(ProductCreationFailedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }
}
