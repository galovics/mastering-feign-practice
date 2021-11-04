package com.arnoldgalovics.user.session.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserSessionController {
    @GetMapping("/user-sessions/validate")
    public ResponseEntity<?> validate(@RequestParam("sessionId") UUID sessionId) {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header("Location", "http://localhost:8082/user-sessions/v2/validate?sessionId=" + sessionId.toString())
                .build();
    }

    @GetMapping("/user-sessions/v2/validate")
    public UserSessionValidatorResponse validateV2(@RequestParam("sessionId") UUID sessionId) {
        boolean isValid = UUID.fromString("ad8614c1-d3e9-4b62-971a-1e7b19345fcb").equals(sessionId);
        return UserSessionValidatorResponse.builder()
                .sessionId(sessionId)
                .valid(isValid)
                .build();
    }
}
