package com.arnoldgalovics.online.store.service.external.session;

import com.arnoldgalovics.online.store.service.external.base.HealthResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserSessionClientFallbackFactory implements FallbackFactory<UserSessionClient> {
    @Override
    public UserSessionClient create(Throwable cause) {
        return new UserSessionClient() {
            @Override
            public UserSessionValidatorResponse validateSession(Map<String, Object> queryMap, Map<String, String> headerMap) {
                return new UserSessionValidatorResponse((String) queryMap.get("sessionId"), false);
            }

            @Override
            public HealthResponse health() {
                return new HealthResponse("DOWN");
            }
        };
    }
}
