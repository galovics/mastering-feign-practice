package com.arnoldgalovics.inventory.service.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private int stock;
    private OffsetDateTime lastBoughtAt;
}
