package com.t24.apiproxy.exception;

public class ApiProxyException extends RuntimeException {
    private final String errorCode;
    private final String userMessage;
    private final String technicalMessage;

    public ApiProxyException(String errorCode, String userMessage, String technicalMessage) {
        super(userMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
    }

    public ApiProxyException(String errorCode, String userMessage, String technicalMessage, Throwable cause) {
        super(userMessage, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
    }

    public String getErrorCode() { return errorCode; }
    public String getUserMessage() { return userMessage; }
    public String getTechnicalMessage() { return technicalMessage; }
}
