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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StopWatch;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
@AutoConfigureMockMvc
public class TimeLimiterTest {
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
    void testTimeLimiterWorks() throws Exception {
        String sessionResponseBody = "{ \"sessionId\": \"2660e2cf-3614-4417-bd97-1118a3013ce8\", \"valid\": true }";

        USER_SESSION_SERVICE.stubFor(
                get(urlPathEqualTo("/user-sessions/validate"))
                        .withQueryParam("sessionId", equalTo("2660e2cf-3614-4417-bd97-1118a3013ce8"))
                        .willReturn(aResponse().withBody(sessionResponseBody).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).withFixedDelay(15000)));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                        .header("X-Session-Id", "2660e2cf-3614-4417-bd97-1118a3013ce8"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(500));
        stopWatch.stop();
        assertThat(stopWatch.getTotalTimeMillis()).isLessThan(10_500);
    }
}
