package com.arnoldgalovics.online.store.service.external.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductInventoryServiceRequest {
    private String name;
    private int initialStock;
}
