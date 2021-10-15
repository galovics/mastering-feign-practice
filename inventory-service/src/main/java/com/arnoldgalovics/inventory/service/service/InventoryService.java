package com.arnoldgalovics.inventory.service.service;

import com.arnoldgalovics.inventory.service.exception.ProductInventoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final Map<UUID, ProductInventory> store = new HashMap<>();

    {
        UUID p1Id = UUID.fromString("5c5edfcc-1870-4445-9875-7870b4694234");
        store.put(p1Id, new ProductInventory(p1Id, 10, null));
    }

    private final ProductService productService;

    public ProductInventory getStock(UUID productId) {
        Product product = productService.find(productId);
        return Optional.ofNullable(store.get(product.getId())).orElseThrow(() -> new ProductInventoryNotFoundException("Product inventory not found with id " + productId));
    }

    public void decrementStock(UUID productId, int decrementBy, OffsetDateTime boughtAt) {
        ProductInventory productInventory = store.get(productId);
        if (productInventory == null) {
            throw new ProductInventoryNotFoundException("Product inventory not found for " + productId);
        }
        productInventory.setStock(productInventory.getStock() - decrementBy);
        if (boughtAt != null) {
            productInventory.setLastBoughtAt(boughtAt);
        }
    }

    public void save(UUID productId, int initialStock) {
        store.put(productId, new ProductInventory(productId, initialStock, null));
    }
}
