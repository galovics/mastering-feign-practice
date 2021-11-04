package com.arnoldgalovics.online.store.service;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CircuitBreakerTest {
    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            return new TestServiceInstanceListSupplier("user-session-service", 8082);
        }
    }

    @RegisterExtension
    static WireMockExtension USER_SESSION_SERVICE = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8082))
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testErrorBasedCircuitBreaking() throws Exception {
        String responseBody = "{ \"sessionId\": \"828bc3cb-52f0-482b-8247-d3db5c87c941\", \"valid\": true}";

        USER_SESSION_SERVICE.stubFor(get(urlPathEqualTo("/user-sessions/validate"))
                .withQueryParam("sessionId", equalTo("828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .willReturn(serviceUnavailable()));

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .andExpect(MockMvcResultMatchers.status().is(500));

        USER_SESSION_SERVICE.stubFor(get(urlPathEqualTo("/user-sessions/validate"))
                .withQueryParam("sessionId", equalTo("828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .willReturn(okJson(responseBody)));

        for (int i = 0; i<4; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .andExpect(MockMvcResultMatchers.status().is(500));

        Thread.sleep(6000);

        for (int i = 0; i<6; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }
    }

    @Test
    public void testSlownessBasedCircuitBreaking() throws Exception {
        String responseBody = "{ \"sessionId\": \"828bc3cb-52f0-482b-8247-d3db5c87c941\", \"valid\": true}";

        USER_SESSION_SERVICE.stubFor(get(urlPathEqualTo("/user-sessions/validate"))
                .withQueryParam("sessionId", equalTo("828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .willReturn(aResponse().withBody(responseBody).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).withFixedDelay(2500)));

        for (int i = 0; i<4; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }

        USER_SESSION_SERVICE.stubFor(get(urlPathEqualTo("/user-sessions/validate"))
                .withQueryParam("sessionId", equalTo("828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .willReturn(okJson(responseBody)));

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .andExpect(MockMvcResultMatchers.status().is(200));


        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                .andExpect(MockMvcResultMatchers.status().is(500));
        Thread.sleep(6000);

        for (int i = 0; i<6; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                            .header("X-Session-Id", "828bc3cb-52f0-482b-8247-d3db5c87c941"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
        }
    }
}
