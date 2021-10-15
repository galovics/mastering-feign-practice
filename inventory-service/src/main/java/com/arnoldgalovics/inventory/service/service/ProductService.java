package com.arnoldgalovics.inventory.service.service;

import com.arnoldgalovics.inventory.service.exception.ProductCreationException;
import com.arnoldgalovics.inventory.service.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final Map<UUID, Product> store = new HashMap<>();

    {
        UUID p1Id = UUID.fromString("5c5edfcc-1870-4445-9875-7870b4694234");
        store.put(p1Id, new Product(p1Id, "Phone"));
    }

    public Product find(UUID productId) {
        return Optional.ofNullable(store.get(productId)).orElseThrow(() -> new ProductNotFoundException("Product not found with id " + productId));
    }

    public Product save(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ProductCreationException("The product cannot be created due to empty name");
        }
        Product newProduct = new Product(UUID.randomUUID(), name);
        store.put(newProduct.getId(), newProduct);
        return newProduct;
    }
}
