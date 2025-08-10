package com.hb.cda.electricitybusiness.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class InvalidMediaTypeException extends RuntimeException {
    public InvalidMediaTypeException(String expectedType, String message) {
        super(message);
    }
}
