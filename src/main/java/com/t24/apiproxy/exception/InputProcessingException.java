package com.t24.apiproxy.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when input processing fails.
 * This includes file parsing errors, parameter validation errors, and format issues.
 */
public class InputProcessingException extends ApiProxyException {
    
    private final String inputSource;
    private final Integer lineNumber;
    private final String parameterName;
    private final String actualValue;
    private final String expectedFormat;
    private final List<String> errors;
    private final InputType inputType;
    
    /**
     * Creates a new InputProcessingException
     * 
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     */
    public InputProcessingException(String userMessage, String technicalMessage) {
        super("INPUT_PROCESSING_ERROR", userMessage, technicalMessage);
        this.inputSource = null;
        this.lineNumber = null;
        this.parameterName = null;
        this.actualValue = null;
        this.expectedFormat = null;
        this.errors = new ArrayList<>();
        this.inputType = InputType.UNKNOWN;
    }
    
    /**
     * Creates a new InputProcessingException with single message (backward compatibility)
     * 
     * @param message User-friendly and technical message
     */
    public InputProcessingException(String message) {
        this(message, message);
    }
    
    /**
     * Creates a new InputProcessingException with a cause
     * 
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     * @param cause The underlying cause
     */
    public InputProcessingException(String userMessage, String technicalMessage, Throwable cause) {
        super("INPUT_PROCESSING_ERROR", userMessage, technicalMessage, cause);
        this.inputSource = null;
        this.lineNumber = null;
        this.parameterName = null;
        this.actualValue = null;
        this.expectedFormat = null;
        this.errors = new ArrayList<>();
        this.inputType = InputType.UNKNOWN;
    }
    
    /**
     * Private constructor for builder
     */
    private InputProcessingException(Builder builder) {
        super(
            builder.errorCode != null ? builder.errorCode : "INPUT_PROCESSING_ERROR",
            builder.userMessage,
            builder.technicalMessage,
            builder.cause
        );
        this.inputSource = builder.inputSource;
        this.lineNumber = builder.lineNumber;
        this.parameterName = builder.parameterName;
        this.actualValue = builder.actualValue;
        this.expectedFormat = builder.expectedFormat;
        this.errors = builder.errors;
        this.inputType = builder.inputType;
        
        // Add context
        if (inputSource != null) addContext("inputSource", inputSource);
        if (lineNumber != null) addContext("lineNumber", lineNumber);
        if (parameterName != null) addContext("parameterName", parameterName);
        if (actualValue != null) addContext("actualValue", actualValue);
        if (expectedFormat != null) addContext("expectedFormat", expectedFormat);
        if (!errors.isEmpty()) addContext("errors", errors);
        addContext("inputType", inputType.toString());
    }
    
    // Getters
    
    public String getInputSource() {
        return inputSource;
    }
    
    public Integer getLineNumber() {
        return lineNumber;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public String getActualValue() {
        return actualValue;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public InputType getInputType() {
        return inputType;
    }
    
    @Override
    public Severity getSeverity() {
        // Input processing errors are usually user errors, not system errors
        return Severity.WARNING;
    }
    
    @Override
    public boolean isRetryable() {
        // Input processing errors are not retryable - user must fix input
        return false;
    }
    
    // Static factory methods
    
    /**
     * Creates an exception for file parsing errors
     */
    public static InputProcessingException forFile(String filePath, int lineNumber, String error) {
        return new Builder()
            .withInputType(InputType.FILE)
            .withInputSource(filePath)
            .withLineNumber(lineNumber)
            .withUserMessage("Error parsing file at line " + lineNumber)
            .withTechnicalMessage(error)
            .build();
    }
    
    /**
     * Creates an exception for file format errors
     */
    public static InputProcessingException forInvalidFileFormat(String filePath, String expectedFormat, String actualFormat) {
        return new Builder()
            .withInputType(InputType.FILE)
            .withInputSource(filePath)
            .withExpectedFormat(expectedFormat)
            .withActualValue(actualFormat)
            .withUserMessage("Invalid file format. Expected: " + expectedFormat)
            .withTechnicalMessage("File '" + filePath + "' has format '" + actualFormat + "' but expected '" + expectedFormat + "'")
            .build();
    }
    
    /**
     * Creates an exception for missing file errors
     */
    public static InputProcessingException forMissingFile(String filePath) {
        return new Builder()
            .withInputType(InputType.FILE)
            .withInputSource(filePath)
            .withUserMessage("Input file not found")
            .withTechnicalMessage("File not found: " + filePath)
            .build();
    }
    
    /**
     * Creates an exception for parameter errors
     */
    public static InputProcessingException forParameter(String parameterName, String actualValue, String expectedFormat) {
        return new Builder()
            .withInputType(InputType.PARAMETER)
            .withParameterName(parameterName)
            .withActualValue(actualValue)
            .withExpectedFormat(expectedFormat)
            .withUserMessage("Invalid parameter: " + parameterName)
            .withTechnicalMessage("Parameter '" + parameterName + "' has value '" + actualValue + "' but expected format: " + expectedFormat)
            .build();
    }
    
    /**
     * Creates an exception for missing required parameter
     */
    public static InputProcessingException forMissingParameter(String parameterName) {
        return new Builder()
            .withInputType(InputType.PARAMETER)
            .withParameterName(parameterName)
            .withUserMessage("Required parameter missing: " + parameterName)
            .withTechnicalMessage("The required parameter '" + parameterName + "' was not provided")
            .build();
    }
    
    /**
     * Creates an exception for format errors
     */
    public static InputProcessingException forFormat(String input, String expectedFormat, Throwable cause) {
        return new Builder()
            .withInputType(InputType.FORMAT)
            .withActualValue(input)
            .withExpectedFormat(expectedFormat)
            .withUserMessage("Invalid input format")
            .withTechnicalMessage("Input '" + input + "' does not match expected format: " + expectedFormat)
            .withCause(cause)
            .build();
    }
    
    /**
     * Creates an exception for JSON parsing errors
     */
    public static InputProcessingException forJsonParsing(String json, Throwable cause) {
        return new Builder()
            .withInputType(InputType.JSON)
            .withActualValue(json)
            .withExpectedFormat("Valid JSON")
            .withUserMessage("Invalid JSON format")
            .withTechnicalMessage("Failed to parse JSON: " + cause.getMessage())
            .withCause(cause)
            .build();
    }
    
    /**
     * Creates an exception for CSV parsing errors
     */
    public static InputProcessingException forCsvParsing(String filePath, int lineNumber, String error) {
        return new Builder()
            .withInputType(InputType.CSV)
            .withInputSource(filePath)
            .withLineNumber(lineNumber)
            .withUserMessage("Error parsing CSV file at line " + lineNumber)
            .withTechnicalMessage(error)
            .build();
    }
    
    /**
     * Creates an exception for multiple errors
     */
    public static InputProcessingException forMultipleErrors(String inputSource, List<String> errors) {
        Builder builder = new Builder()
            .withInputType(InputType.MULTIPLE)
            .withInputSource(inputSource)
            .withUserMessage(errors.size() + " error(s) found in input")
            .withTechnicalMessage("Multiple validation errors occurred");
        
        for (String error : errors) {
            builder.addError(error);
        }
        
        return builder.build();
    }
    
    /**
     * Builder for creating InputProcessingException instances
     */
    public static class Builder {
        private String errorCode;
        private String userMessage;
        private String technicalMessage;
        private Throwable cause;
        private String inputSource;
        private Integer lineNumber;
        private String parameterName;
        private String actualValue;
        private String expectedFormat;
        private List<String> errors = new ArrayList<>();
        private InputType inputType = InputType.UNKNOWN;
        
        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder withUserMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }
        
        public Builder withTechnicalMessage(String technicalMessage) {
            this.technicalMessage = technicalMessage;
            return this;
        }
        
        public Builder withCause(Throwable cause) {
            this.cause = cause;
            return this;
        }
        
        public Builder withInputSource(String inputSource) {
            this.inputSource = inputSource;
            return this;
        }
        
        public Builder withLineNumber(Integer lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }
        
        public Builder withParameterName(String parameterName) {
            this.parameterName = parameterName;
            return this;
        }
        
        public Builder withActualValue(String actualValue) {
            this.actualValue = actualValue;
            return this;
        }
        
        public Builder withExpectedFormat(String expectedFormat) {
            this.expectedFormat = expectedFormat;
            return this;
        }
        
        public Builder withInputType(InputType inputType) {
            this.inputType = inputType;
            return this;
        }
        
        public Builder addError(String error) {
            this.errors.add(error);
            return this;
        }
        
        public InputProcessingException build() {
            return new InputProcessingException(this);
        }
    }
    
    /**
     * Type of input being processed
     */
    public enum InputType {
        FILE,           // File input
        PARAMETER,      // Command-line parameter
        JSON,           // JSON input
        CSV,            // CSV file
        TEXT,           // Text file
        FORMAT,         // Format parsing
        MULTIPLE,       // Multiple errors
        UNKNOWN         // Unknown type
    }
}
