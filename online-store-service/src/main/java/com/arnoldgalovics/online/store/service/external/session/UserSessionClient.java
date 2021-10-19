package com.arnoldgalovics.online.store.service.external.session;

import com.arnoldgalovics.online.store.service.external.base.BaseClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FeignClient(value = "user-session-service")
public interface UserSessionClient extends BaseClient {
    @GetMapping("/user-sessions/validate")
    UserSessionValidatorResponse validateSession(@RequestParam Map<String, Object> queryMap, @RequestHeader Map<String, String> headerMap);

    default UserSessionValidatorResponse validateSession(UUID sessionId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("sessionId", sessionId.toString());
        return validateSession(queryMap, Collections.singletonMap("X-Source", "online-store-service"));
    }
}
