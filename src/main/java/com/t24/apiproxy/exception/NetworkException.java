package com.t24.apiproxy.exception;

public class NetworkException extends ApiProxyException {
    public NetworkException(String code, String userMsg, String techMsg) {
        super(code, userMsg, techMsg);
    }
    public NetworkException(String code, String userMsg, String techMsg, Throwable cause) {
        super(code, userMsg, techMsg, cause);
    }
}
