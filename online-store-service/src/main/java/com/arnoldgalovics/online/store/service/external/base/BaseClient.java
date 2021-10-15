package com.arnoldgalovics.online.store.service.external.base;

import feign.RequestLine;

public interface BaseClient {
    @RequestLine("GET /actuator/health")
    HealthResponse health();
}
