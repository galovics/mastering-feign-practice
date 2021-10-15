package com.arnoldgalovics.inventory.service.api;

import com.arnoldgalovics.inventory.service.api.model.AddProductRequest;
import com.arnoldgalovics.inventory.service.api.model.AddProductResponse;
import com.arnoldgalovics.inventory.service.api.model.ProductResponse;
import com.arnoldgalovics.inventory.service.api.model.ProductStockResponse;
import com.arnoldgalovics.inventory.service.service.InventoryService;
import com.arnoldgalovics.inventory.service.service.Product;
import com.arnoldgalovics.inventory.service.service.ProductInventory;
import com.arnoldgalovics.inventory.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InventoryApi {
    private final ProductService productService;
    private final InventoryService inventoryService;

    @GetMapping("/inventory/products/{productId}/stock")
    public ProductStockResponse getProductStock(@PathVariable UUID productId) {
        ProductInventory productInventory = inventoryService.getStock(productId);
        String formattedLastBoughtAt = null;
        OffsetDateTime lastBoughtAt = productInventory.getLastBoughtAt();
        if (lastBoughtAt != null) {
            formattedLastBoughtAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(lastBoughtAt);
        }
        return ProductStockResponse.builder().productId(productId).stock(productInventory.getStock()).lastBoughtAt(formattedLastBoughtAt).build();
    }

    @PostMapping("/inventory/products/{productId}/decrement")
    public ResponseEntity<?> decrementStockForProduct(@PathVariable UUID productId,
                                                      @RequestParam(name = "decrementBy", defaultValue = "1") int decrementBy,
                                                      @RequestParam(name = "boughtAt", required = false) OffsetDateTime boughtAt) {
        inventoryService.decrementStock(productId, decrementBy, boughtAt);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products/{productId}")
    public ProductResponse getProduct(@PathVariable UUID productId) {
        Product product = productService.find(productId);
        return ProductResponse.builder().productId(product.getId()).name(product.getName()).build();
    }

    @PostMapping("/products")
    public AddProductResponse addProduct(@RequestBody AddProductRequest addProductRequest) {
        Product newProduct = productService.save(addProductRequest.getName());
        inventoryService.save(newProduct.getId(), addProductRequest.getInitialStock());
        return AddProductResponse.builder().productId(newProduct.getId()).name(newProduct.getName()).stock(addProductRequest.getInitialStock()).build();
    }
}
