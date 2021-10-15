package com.arnoldgalovics.online.store.service.external.config;

import com.arnoldgalovics.online.store.service.util.RequestHeaderExtractor;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import static com.arnoldgalovics.online.store.service.util.Constants.X_SESSION_ID_HEADER_NAME;

public class SessionIdRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String sessionId = RequestHeaderExtractor.getHeader(X_SESSION_ID_HEADER_NAME);
        template.header(X_SESSION_ID_HEADER_NAME, sessionId);
    }
}
