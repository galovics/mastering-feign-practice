package com.arnoldgalovics.online.store.service;

import com.arnoldgalovics.online.store.service.external.inventory.AddProductInventoryServiceRequest;
import com.arnoldgalovics.online.store.service.external.inventory.AddProductInventoryServiceResponse;
import com.arnoldgalovics.online.store.service.external.inventory.InventoryServiceClient;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
class InventoryServiceTest {

    @RegisterExtension
    static WireMockExtension INVENTORY_SERVICE = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8082))
            .build();

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Test
    public void testInventoryServiceGetsCalled() {
        String responseBody = "{ \"productId\": \"2660e2cf-3614-4417-bd97-1118a3013ce8\", \"name\": \"Mock name\", \"stock\": 1}";
        INVENTORY_SERVICE.stubFor(WireMock.post("/products").willReturn(WireMock.okJson(responseBody)));

        AddProductInventoryServiceResponse response = inventoryServiceClient.addProduct(new AddProductInventoryServiceRequest("bla", 25));

        assertThat(response.getName()).isEqualTo("Mock name");
        assertThat(response.getStock()).isEqualTo(1);
    }
}
