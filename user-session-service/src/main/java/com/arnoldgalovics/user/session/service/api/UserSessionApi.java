package com.arnoldgalovics.user.session.service.api;

import com.arnoldgalovics.user.session.service.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserSessionApi {
    @Value("${server.port}")
    private String serverPort;

    private final UserSessionService userSessionService;

    @GetMapping("/user-sessions/validate")
    public ResponseEntity<?> validateSession(@RequestParam("sessionId") UUID sessionId) {
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, "http://localhost:" + serverPort + "/user-sessions/v2/validate?sessionId=" + sessionId)
                .build();
    }

    @GetMapping("/user-sessions/v2/validate")
    public UserSessionValidatorResponse validateSession2(@RequestParam("sessionId") UUID sessionId, @RequestHeader(value = "X-Sleep", defaultValue = "3000") int toSleep) throws Exception {
        log.info("Sleeping for {} ms on server {}", toSleep, serverPort);
        Thread.sleep(toSleep);
        boolean isSessionValid = userSessionService.isSessionValid(sessionId);
        return UserSessionValidatorResponse.builder()
                .sessionId(sessionId)
                .valid(isSessionValid)
                .build();
    }
}
