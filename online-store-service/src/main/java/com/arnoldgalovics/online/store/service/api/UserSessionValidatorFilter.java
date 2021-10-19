package com.arnoldgalovics.online.store.service.api;

import com.arnoldgalovics.online.store.service.external.session.UserSessionClient;
import com.arnoldgalovics.online.store.service.external.session.UserSessionValidatorResponse;
import com.arnoldgalovics.online.store.service.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("sessionId", sessionId);
            Map<String, String> headerMap = new HashMap<>();
            String header = httpServletRequest.getHeader("X-Sleep");
            if (StringUtils.hasText(header)) {
                headerMap.put("X-Sleep", header);
            }
            // The try catch is needed because the exceptions thrown in filters are not handled by the @ExceptionHandlers
            try {
                UserSessionValidatorResponse userSessionValidatorResponse = userSessionClient.validateSession(queryMap, headerMap);
                if (!userSessionValidatorResponse.isValid()) {
                    httpServletResponse.sendError(HttpStatus.FORBIDDEN.value());
                } else {
                    chain.doFilter(request, response);
                }
            } catch (Exception e) {
                log.error("Error while executing session validator filter", e);
                httpServletResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }
}
