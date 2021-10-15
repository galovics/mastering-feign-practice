package com.arnoldgalovics.inventory.service.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductStockResponse {
    private UUID productId;
    private int stock;
    private String lastBoughtAt;
}
