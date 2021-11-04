package com.arnoldgalovics.online.store.service.external.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class SourceRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Source", "online-store-service");
    }
}
