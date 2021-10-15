package com.arnoldgalovics.inventory.service.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddProductResponse {
    private UUID productId;
    private String name;
    private int stock;
}
