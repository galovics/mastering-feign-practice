package com.arnoldgalovics.online.store.service.external.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionValidatorResponse {
    private String sessionId;
    private boolean valid;
}