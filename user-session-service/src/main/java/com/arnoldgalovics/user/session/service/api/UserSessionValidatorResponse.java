package com.arnoldgalovics.user.session.service.api;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class UserSessionValidatorResponse {
    private UUID sessionId;
    private boolean valid;
}
