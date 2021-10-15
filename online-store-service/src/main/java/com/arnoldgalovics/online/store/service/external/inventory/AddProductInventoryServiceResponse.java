package com.arnoldgalovics.online.store.service.external.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductInventoryServiceResponse {
    private UUID productId;
    private String name;
    private int stock;
}
