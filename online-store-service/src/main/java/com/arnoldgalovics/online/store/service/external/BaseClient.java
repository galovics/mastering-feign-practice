package com.arnoldgalovics.online.store.service.external;

import feign.RequestLine;

public interface BaseClient {
    @RequestLine("GET /actuator/health")
    ActuatorHealthResponse health();
}
