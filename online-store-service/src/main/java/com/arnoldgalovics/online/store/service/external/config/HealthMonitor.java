package com.arnoldgalovics.online.store.service.external.config;

import com.arnoldgalovics.online.store.service.external.base.BaseClient;
import com.arnoldgalovics.online.store.service.external.base.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class HealthMonitor {
    private final Collection<BaseClient> clients;

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void monitorExternalServices() {
        clients.forEach(c -> {
            HealthResponse healthResponse = new HealthResponse("DOWN");
            try {
                healthResponse = c.health();
            } catch (Exception e) {
                log.debug("Exception when calling the health API", e);
            }
            log.info("Status for {} is {}", c.getClass().getInterfaces()[0].getSimpleName(), healthResponse.getStatus());
        });
    }
}
