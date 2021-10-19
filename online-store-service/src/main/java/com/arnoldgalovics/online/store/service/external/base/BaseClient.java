package com.arnoldgalovics.online.store.service.external.base;

import org.springframework.web.bind.annotation.GetMapping;

public interface BaseClient {
    @GetMapping("/actuator/health")
    HealthResponse health();
}
