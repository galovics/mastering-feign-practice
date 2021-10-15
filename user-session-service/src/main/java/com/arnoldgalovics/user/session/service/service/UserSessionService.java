package com.arnoldgalovics.user.session.service.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserSessionService {
    public boolean isSessionValid(UUID sessionId) {
        return UUID.fromString("ad8614c1-d3e9-4b62-971a-1e7b19345fcb").equals(sessionId);
    }
}
