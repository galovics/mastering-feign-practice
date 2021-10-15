package com.arnoldgalovics.online.store.service.external.inventory;

import com.arnoldgalovics.online.store.service.external.base.BaseClient;
import com.arnoldgalovics.online.store.service.external.expander.OffsetDateTimeToMillisExpander;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface InventoryServiceClient extends BaseClient {
    @RequestLine("POST /inventory/products/{productId}/decrement?decrementBy={decrementBy}&boughtAt={boughtAt}")
    void decrementStockForProduct(@Param("productId") UUID productId,
                                  @Param("decrementBy") int decrementBy,
                                  @Param(value = "boughtAt", expander = OffsetDateTimeToMillisExpander.class) OffsetDateTime boughtAt);

    @RequestLine("POST /products")
    @Headers("Content-Type: application/json")
    AddProductInventoryServiceResponse addProduct(AddProductInventoryServiceRequest inventoryServiceRequest);
}
