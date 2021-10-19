package com.arnoldgalovics.online.store.service.external.config;

import feign.Feign;
import feign.Target;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

// This is fixed already in the library, although it's not yet released
@Component
public class FeignClientCircuitBreakerNameResolver implements CircuitBreakerNameResolver {
    @Override
    public String resolveCircuitBreakerName(String feignClientName, Target<?> target, Method method) {
        return Feign.configKey(target.type(), method);
    }
}
