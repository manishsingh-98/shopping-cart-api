package com.mss.cart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponseException extends RuntimeException {

    private HttpStatus status;

    public ErrorResponseException(final String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ErrorResponseException(final String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
