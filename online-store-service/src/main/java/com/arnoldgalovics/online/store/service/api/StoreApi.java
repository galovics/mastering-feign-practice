package com.arnoldgalovics.online.store.service.api;

import com.arnoldgalovics.online.store.service.external.inventory.AddProductInventoryServiceRequest;
import com.arnoldgalovics.online.store.service.external.inventory.AddProductInventoryServiceResponse;
import com.arnoldgalovics.online.store.service.external.inventory.InventoryServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StoreApi {
    private final InventoryServiceClient inventoryServiceClient;

    @PostMapping("/products/{productId}/buy")
    public ResponseEntity<?> buy(@PathVariable("productId") UUID productId) {
        OffsetDateTime boughtAt = OffsetDateTime.now(Clock.systemUTC());
        inventoryServiceClient.decrementStockForProduct(productId, 1, boughtAt);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/add")
    public AddProductResponse add(@RequestBody AddProductRequest addProductRequest) {
        AddProductInventoryServiceRequest inventoryServiceRequest = new AddProductInventoryServiceRequest();
        inventoryServiceRequest.setName(addProductRequest.getName());
        inventoryServiceRequest.setInitialStock(addProductRequest.getInitialStock());
        AddProductInventoryServiceResponse inventoryServiceResponse = inventoryServiceClient.addProduct(inventoryServiceRequest);
        return AddProductResponse.builder()
                .productId(inventoryServiceResponse.getProductId())
                .name(inventoryServiceResponse.getName())
                .stock(inventoryServiceResponse.getStock()).build();
    }
}
