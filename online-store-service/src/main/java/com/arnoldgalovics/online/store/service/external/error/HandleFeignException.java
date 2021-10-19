package com.arnoldgalovics.online.store.service.external.error;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleFeignException {
    Class<? extends FeignHttpExceptionHandler> value();
}