package com.arnoldgalovics.inventory.service.api;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateProductResponse {
    private UUID productId;
    private String name;
    private int stock;
}
