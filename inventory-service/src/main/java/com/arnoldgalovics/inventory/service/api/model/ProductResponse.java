package com.arnoldgalovics.inventory.service.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID productId;
    private String name;
}
