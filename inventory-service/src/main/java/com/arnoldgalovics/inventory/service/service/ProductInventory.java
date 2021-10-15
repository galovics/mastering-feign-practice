package com.arnoldgalovics.inventory.service.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductInventory {
    private final UUID productId;
    private int stock;
    private OffsetDateTime lastBoughtAt;
}
