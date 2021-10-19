package com.arnoldgalovics.online.store.service.external.inventory;

import com.arnoldgalovics.online.store.service.external.error.HandleFeignException;
import com.arnoldgalovics.online.store.service.external.error.InventoryServiceExceptionHandler;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.util.UUID;

@FeignClient(name = "inventory-service", url = "http://localhost:8082")
public interface InventoryServiceClient {
    @PostMapping("/inventory/products/{productId}/decrement")
    void decrementStockForProduct(@PathVariable("productId") UUID productId,
                                  @RequestParam("decrementBy") int decrementBy,
                                  @RequestParam(value = "boughtAt") OffsetDateTime boughtAt);

    @PostMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    @HandleFeignException(InventoryServiceExceptionHandler.class)
    AddProductInventoryServiceResponse addProduct(AddProductInventoryServiceRequest inventoryServiceRequest);
}
