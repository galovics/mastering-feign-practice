package com.arnoldgalovics.online.store.service.external.expander;

import feign.Param;

import java.time.OffsetDateTime;

public class OffsetDateTimeToMillisExpander implements Param.Expander {
    @Override
    public String expand(Object value) {
        if (!OffsetDateTime.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Non OffsetDateTime argument has been passed");
        }
        long millis = ((OffsetDateTime) value).toInstant().toEpochMilli();
        return "" + millis;
    }
}
