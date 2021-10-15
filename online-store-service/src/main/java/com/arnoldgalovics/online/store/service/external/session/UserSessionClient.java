package com.arnoldgalovics.online.store.service.external.session;

import com.arnoldgalovics.online.store.service.external.base.BaseClient;
import feign.HeaderMap;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserSessionClient extends BaseClient {
    @RequestLine("GET /user-sessions/validate/{sessionId}")
    CompletableFuture<UserSessionValidatorResponse> validateSession(@QueryMap Map<String, Object> queryMap, @HeaderMap Map<String, Object> headerMap);

    default CompletableFuture<UserSessionValidatorResponse> validateSession(UUID sessionId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("sessionId", sessionId.toString());
        return validateSession(queryMap, Collections.singletonMap("X-Source", "online-store-service"));
    }
}
