package com.t24.apiproxy.model;

public class ErrorResponse {
    private String errorCode;
    private String userMessage;
    private String technicalMessage;
    private String timestamp;
    private String executionId;

    // getters & setters
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getUserMessage() {
        return userMessage;
    }
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    public String getTechnicalMessage() {
        return technicalMessage;
    }
    public void setTechnicalMessage(String technicalMessage) {
        this.technicalMessage = technicalMessage;
    }
    public String getTimestamp() {      
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getExecutionId() {
        return executionId;
    }
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }  
    @Override
    public String toString() {
        return "ErrorResponse{" +
               "errorCode='" + errorCode + '\'' +
               ", userMessage='" + userMessage + '\'' +
               ", technicalMessage='" + technicalMessage + '\'' +
               ", timestamp='" + timestamp + '\'' +
               ", executionId='" + executionId + '\'' +
               '}'; 
    }
}
