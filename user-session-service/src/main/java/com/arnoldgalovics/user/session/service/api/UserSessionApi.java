package com.arnoldgalovics.user.session.service.api;

import com.arnoldgalovics.user.session.service.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserSessionApi {
    private final UserSessionService userSessionService;

    @GetMapping("/user-sessions/validate")
    public ResponseEntity<?> validateSession(@RequestParam("sessionId") UUID sessionId) {
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, "http://localhost:8081/user-sessions/v2/validate?sessionId=" + sessionId)
                .build();
    }

    @GetMapping("/user-sessions/v2/validate")
    public UserSessionValidatorResponse validateSession2(@RequestParam("sessionId") UUID sessionId) {
        boolean isSessionValid = userSessionService.isSessionValid(sessionId);
        return UserSessionValidatorResponse.builder()
                .sessionId(sessionId)
                .valid(isSessionValid)
                .build();
    }
}
