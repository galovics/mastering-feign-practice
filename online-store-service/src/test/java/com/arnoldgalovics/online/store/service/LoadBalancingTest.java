package com.arnoldgalovics.online.store.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
@AutoConfigureMockMvc
class LoadBalancingTest {
    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            return new TestServiceInstanceListSupplier("user-session-service", 8081, 9091);
        }
    }

    @RegisterExtension
    static WireMockExtension INVENTORY_SERVICE = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8082))
            .build();

    @RegisterExtension
    static WireMockExtension USER_SESSION_SERVICE_1 = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8081))
            .build();


    @RegisterExtension
    static WireMockExtension USER_SESSION_SERVICE_2 = WireMockExtension.newInstance()
            .options(wireMockConfig().port(9091))
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateNewProductRoundRobinLoadBalancingWorks() throws Exception {
        String responseBody = "{ \"productId\": \"2660e2cf-3614-4417-bd97-1118a3013ce8\", \"name\": \"Mock name\", \"stock\": 1}";
        INVENTORY_SERVICE.stubFor(WireMock.post("/products").willReturn(okJson(responseBody)));

        String sessionResponseBody = "{ \"sessionId\": \"2660e2cf-3614-4417-bd97-1118a3013ce8\", \"valid\": true }";
        String invalidSessionResponseBody = "{ \"sessionId\": \"2660e2cf-3614-4417-bd97-1118a3013ce8\", \"valid\": false }";
        USER_SESSION_SERVICE_1.stubFor(
                get(urlPathEqualTo("/user-sessions/validate"))
                        .withQueryParam("sessionId", equalTo("2660e2cf-3614-4417-bd97-1118a3013ce8"))
                        .willReturn(okJson(sessionResponseBody)));
        USER_SESSION_SERVICE_2.stubFor(
                get(urlPathEqualTo("/user-sessions/validate"))
                        .withQueryParam("sessionId", equalTo("2660e2cf-3614-4417-bd97-1118a3013ce8"))
                        .willReturn(okJson(invalidSessionResponseBody)));

        String createRequestBody = "{ \"name\": \"Mock product name\", \"stock\": 2}";
        int firstStatus = mockMvc.perform(MockMvcRequestBuilders.post("/products/add")
                        .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8")
                        .content(createRequestBody).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();

        int secondStatus = mockMvc.perform(MockMvcRequestBuilders.post("/products/add")
                        .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8")
                        .content(createRequestBody).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();

        assertThat(List.of(firstStatus, secondStatus)).containsExactlyInAnyOrder(200, 403);
    }

}
