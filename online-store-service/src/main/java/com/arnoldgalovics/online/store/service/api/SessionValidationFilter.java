package com.arnoldgalovics.online.store.service.api;

import com.arnoldgalovics.online.store.service.external.session.UserSessionClient;
import com.arnoldgalovics.online.store.service.external.session.UserSessionValidatorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionValidationFilter implements Filter {
    private final UserSessionClient userSessionClient;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String sessionIdHeader = httpServletRequest.getHeader("X-Session-Id");
        if (sessionIdHeader == null) {
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value());
        } else {
            String sleepTime = httpServletRequest.getHeader("X-Sleep");
            Map<String, Object> headerMap = new HashMap<>();
            if (sleepTime != null) {
                headerMap.put("X-Sleep", sleepTime);
            }
            UUID sessionIdUUID = UUID.fromString(sessionIdHeader);
            try {
                UserSessionValidatorResponse userSessionValidatorResponse = userSessionClient.validateSession(sessionIdUUID, headerMap);
                if (!userSessionValidatorResponse.isValid()) {
                    httpServletResponse.sendError(HttpStatus.FORBIDDEN.value());
                } else {
                    chain.doFilter(request, response);
                }
            } catch (Exception e) {
                log.error("Exception while executing the session validator filter", e);
                httpServletResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }

    }
}
