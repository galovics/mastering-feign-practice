package com.arnoldgalovics.online.store.service.api;

import com.arnoldgalovics.online.store.service.external.session.UserSessionClient;
import com.arnoldgalovics.online.store.service.external.session.UserSessionValidatorResponse;
import com.arnoldgalovics.online.store.service.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSessionValidatorFilter implements Filter {
    private final UserSessionClient userSessionClient;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String sessionId = httpServletRequest.getHeader(Constants.X_SESSION_ID_HEADER_NAME);
        if (sessionId == null) {
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value());
        } else {
            CompletableFuture<UserSessionValidatorResponse> sessResponse = userSessionClient.validateSession(UUID.fromString(sessionId));
            log.info("Doing some expensive computation..");
            UserSessionValidatorResponse userSessionValidatorResponse = null;
            try {
                userSessionValidatorResponse = sessResponse.get();
                log.info("Response received for session validation");
            } catch (Exception e) {
                throw new RuntimeException("Error while validating the session", e);
            }
            if (!userSessionValidatorResponse.isValid()) {
                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value());
            } else {
                chain.doFilter(request, response);
            }
        }
    }
}
