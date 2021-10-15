package com.arnoldgalovics.online.store.service.external.session;

import lombok.Data;

@Data
public class UserSessionValidatorResponse {
    private String sessionId;
    private boolean valid;
}