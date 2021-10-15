package com.arnoldgalovics.online.store.service.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductResponse {
    private UUID productId;
    private String name;
    private int stock;
}
