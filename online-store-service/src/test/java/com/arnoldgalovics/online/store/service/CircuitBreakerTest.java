package com.arnoldgalovics.online.store.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CircuitBreakerTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            return new TestServiceInstanceListSupplier("user-session-service", 8081);
        }
    }

    @RegisterExtension
    static WireMockExtension USER_SESSION_SERVICE = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8081))
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testErroringCircuitBreakerWorks() throws Exception {
        USER_SESSION_SERVICE.stubFor(
                get(urlPathEqualTo("/user-sessions/validate"))
                        .withQueryParam("sessionId", equalTo("2660e2cf-3614-4417-bd97-1118a3013ce8"))
                        .willReturn(serviceUnavailable()));

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                .andExpect(MockMvcResultMatchers.status().is(500));

        String sessionResponseBody = "{ \"sessionId\": \"2660e2cf-3614-4417-bd97-1118a3013ce8\", \"valid\": true }";

        USER_SESSION_SERVICE.stubFor(
                get(urlPathEqualTo("/user-sessions/validate"))
                        .withQueryParam("sessionId", equalTo("2660e2cf-3614-4417-bd97-1118a3013ce8"))
                        .willReturn(okJson(sessionResponseBody)));

        for (int i = 0; i < 4; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(500));
        Thread.sleep(6000);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }
    }

    @Test
    void testSlownessCircuitBreakerWorks() throws Exception {

        String sessionResponseBody = "{ \"sessionId\": \"2660e2cf-3614-4417-bd97-1118a3013ce8\", \"valid\": true }";

        USER_SESSION_SERVICE.stubFor(
                get(urlPathEqualTo("/user-sessions/validate"))
                        .withQueryParam("sessionId", equalTo("2660e2cf-3614-4417-bd97-1118a3013ce8"))
                        .willReturn(aResponse().withBody(sessionResponseBody).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).withFixedDelay(6000)));

        for (int i = 0; i < 4; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }

        USER_SESSION_SERVICE.stubFor(
                get(urlPathEqualTo("/user-sessions/validate"))
                        .withQueryParam("sessionId", equalTo("2660e2cf-3614-4417-bd97-1118a3013ce8"))
                        .willReturn(okJson(sessionResponseBody)));

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                .andExpect(MockMvcResultMatchers.status().is(200));

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(500));
        Thread.sleep(6000);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }
    }
}
