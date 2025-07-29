package com.t24.apiproxy.exception;

public class InputProcessingException extends ApiProxyException {
    public InputProcessingException(String msg) {
        super("INPUT_PROCESSING_ERROR", msg, msg);
    }
}
