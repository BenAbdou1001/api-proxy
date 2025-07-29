package com.t24.apiproxy.exception;

public class ValidationException extends ApiProxyException {
    public ValidationException(String msg) {
        super("VALIDATION_ERROR", msg, msg);
    }
}
