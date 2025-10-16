package com.t24.apiproxy.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exception thrown when validation fails.
 * This includes request validation, parameter validation, and business rule violations.
 */
public class ValidationException extends ApiProxyException {
    
    private final String fieldName;
    private final Object actualValue;
    private final String expectedFormat;
    private final List<ConstraintViolation> violations;
    private final ValidationType validationType;
    private final Map<String, Object> validationContext;
    
    /**
     * Creates a new ValidationException
     * 
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     */
    public ValidationException(String userMessage, String technicalMessage) {
        super("VALIDATION_ERROR", userMessage, technicalMessage);
        this.fieldName = null;
        this.actualValue = null;
        this.expectedFormat = null;
        this.violations = new ArrayList<>();
        this.validationType = ValidationType.GENERAL;
        this.validationContext = new HashMap<>();
    }
    
    /**
     * Creates a new ValidationException with single message (backward compatibility)
     * 
     * @param message User-friendly and technical message
     */
    public ValidationException(String message) {
        this(message, message);
    }
    
    /**
     * Creates a new ValidationException with a cause
     * 
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     * @param cause The underlying cause
     */
    public ValidationException(String userMessage, String technicalMessage, Throwable cause) {
        super("VALIDATION_ERROR", userMessage, technicalMessage, cause);
        this.fieldName = null;
        this.actualValue = null;
        this.expectedFormat = null;
        this.violations = new ArrayList<>();
        this.validationType = ValidationType.GENERAL;
        this.validationContext = new HashMap<>();
    }
    
    /**
     * Private constructor for builder
     */
    private ValidationException(Builder builder) {
        super(
            builder.errorCode != null ? builder.errorCode : "VALIDATION_ERROR",
            builder.userMessage,
            builder.technicalMessage,
            builder.cause
        );
        this.fieldName = builder.fieldName;
        this.actualValue = builder.actualValue;
        this.expectedFormat = builder.expectedFormat;
        this.violations = builder.violations;
        this.validationType = builder.validationType;
        this.validationContext = builder.validationContext;
        
        // Add context
        if (fieldName != null) addContext("fieldName", fieldName);
        if (actualValue != null) addContext("actualValue", actualValue.toString());
        if (expectedFormat != null) addContext("expectedFormat", expectedFormat);
        if (!violations.isEmpty()) {
            List<String> violationMessages = new ArrayList<>();
            for (ConstraintViolation violation : violations) {
                violationMessages.add(violation.toString());
            }
            addContext("violations", violationMessages);
        }
        addContext("validationType", validationType.toString());
        if (!validationContext.isEmpty()) addContext("validationContext", validationContext);
    }
    
    // Getters
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getActualValue() {
        return actualValue;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    public List<ConstraintViolation> getViolations() {
        return new ArrayList<>(violations);
    }
    
    public ValidationType getValidationType() {
        return validationType;
    }
    
    public Map<String, Object> getValidationContext() {
        return new HashMap<>(validationContext);
    }
    
    @Override
    public Severity getSeverity() {
        // Validation errors are typically user errors
        return Severity.WARNING;
    }
    
    @Override
    public boolean isRetryable() {
        // Validation errors are not retryable - user must fix input
        return false;
    }
    
    /**
     * Checks if there are any violations
     */
    public boolean hasViolations() {
        return !violations.isEmpty();
    }
    
    /**
     * Gets the number of violations
     */
    public int getViolationCount() {
        return violations.size();
    }
    
    /**
     * Gets violations for a specific field
     */
    public List<ConstraintViolation> getViolationsForField(String fieldName) {
        List<ConstraintViolation> fieldViolations = new ArrayList<>();
        for (ConstraintViolation violation : violations) {
            if (fieldName.equals(violation.getFieldName())) {
                fieldViolations.add(violation);
            }
        }
        return fieldViolations;
    }
    
    // Static factory methods
    
    /**
     * Creates a ValidationException for a single field
     */
    public static ValidationException forField(String fieldName, Object actualValue, String expectedFormat) {
        return new Builder()
            .withValidationType(ValidationType.FIELD)
            .withFieldName(fieldName)
            .withActualValue(actualValue)
            .withExpectedFormat(expectedFormat)
            .withUserMessage("Validation failed for field: " + fieldName)
            .withTechnicalMessage("Field '" + fieldName + "' has value '" + actualValue + "' but expected: " + expectedFormat)
            .build();
    }
    
    /**
     * Creates a ValidationException for required field
     */
    public static ValidationException forRequiredField(String fieldName) {
        return new Builder()
            .withValidationType(ValidationType.REQUIRED)
            .withFieldName(fieldName)
            .withUserMessage("Required field missing: " + fieldName)
            .withTechnicalMessage("The required field '" + fieldName + "' must be provided")
            .build();
    }
    
    /**
     * Creates a ValidationException for URL validation
     */
    public static ValidationException forInvalidUrl(String url, String reason) {
        return new Builder()
            .withValidationType(ValidationType.URL)
            .withFieldName("url")
            .withActualValue(url)
            .withExpectedFormat("Valid URL")
            .withUserMessage("Invalid URL: " + reason)
            .withTechnicalMessage("URL '" + url + "' is invalid: " + reason)
            .build();
    }
    
    /**
     * Creates a ValidationException for HTTP method validation
     */
    public static ValidationException forInvalidMethod(String method, List<String> validMethods) {
        return new Builder()
            .withValidationType(ValidationType.HTTP_METHOD)
            .withFieldName("method")
            .withActualValue(method)
            .withExpectedFormat("One of: " + String.join(", ", validMethods))
            .withUserMessage("Invalid HTTP method: " + method)
            .withTechnicalMessage("Method '" + method + "' is not valid. Expected one of: " + String.join(", ", validMethods))
            .build();
    }
    
    /**
     * Creates a ValidationException for range validation
     */
    public static ValidationException forOutOfRange(String fieldName, Object value, Object min, Object max) {
        return new Builder()
            .withValidationType(ValidationType.RANGE)
            .withFieldName(fieldName)
            .withActualValue(value)
            .withExpectedFormat("Between " + min + " and " + max)
            .withUserMessage("Value out of range: " + fieldName)
            .withTechnicalMessage("Field '" + fieldName + "' has value " + value + " but must be between " + min + " and " + max)
            .build();
    }
    
    /**
     * Creates a ValidationException for format validation
     */
    public static ValidationException forInvalidFormat(String fieldName, Object value, String expectedFormat, String pattern) {
        return new Builder()
            .withValidationType(ValidationType.FORMAT)
            .withFieldName(fieldName)
            .withActualValue(value)
            .withExpectedFormat(expectedFormat)
            .withUserMessage("Invalid format for " + fieldName)
            .withTechnicalMessage("Field '" + fieldName + "' has value '" + value + "' which doesn't match pattern: " + pattern)
            .addValidationContext("pattern", pattern)
            .build();
    }
    
    /**
     * Creates a ValidationException for length validation
     */
    public static ValidationException forInvalidLength(String fieldName, int actualLength, int minLength, int maxLength) {
        return new Builder()
            .withValidationType(ValidationType.LENGTH)
            .withFieldName(fieldName)
            .withActualValue(actualLength)
            .withExpectedFormat("Length between " + minLength + " and " + maxLength)
            .withUserMessage("Invalid length for " + fieldName)
            .withTechnicalMessage("Field '" + fieldName + "' has length " + actualLength + " but must be between " + minLength + " and " + maxLength)
            .build();
    }
    
    /**
     * Creates a ValidationException for authentication validation
     */
    public static ValidationException forInvalidAuth(String authType, String reason) {
        return new Builder()
            .withValidationType(ValidationType.AUTHENTICATION)
            .withFieldName("authType")
            .withActualValue(authType)
            .withUserMessage("Invalid authentication: " + reason)
            .withTechnicalMessage("Authentication type '" + authType + "' is invalid: " + reason)
            .build();
    }
    
    /**
     * Creates a ValidationException for multiple violations
     */
    public static ValidationException forMultipleViolations(List<ConstraintViolation> violations) {
        Builder builder = new Builder()
            .withValidationType(ValidationType.MULTIPLE)
            .withUserMessage(violations.size() + " validation error(s)")
            .withTechnicalMessage("Multiple validation errors occurred");
        
        for (ConstraintViolation violation : violations) {
            builder.addViolation(violation);
        }
        
        return builder.build();
    }
    
    /**
     * Builder for creating ValidationException instances
     */
    public static class Builder {
        private String errorCode;
        private String userMessage;
        private String technicalMessage;
        private Throwable cause;
        private String fieldName;
        private Object actualValue;
        private String expectedFormat;
        private List<ConstraintViolation> violations = new ArrayList<>();
        private ValidationType validationType = ValidationType.GENERAL;
        private Map<String, Object> validationContext = new HashMap<>();
        
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
        
        public Builder withFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }
        
        public Builder withActualValue(Object actualValue) {
            this.actualValue = actualValue;
            return this;
        }
        
        public Builder withExpectedFormat(String expectedFormat) {
            this.expectedFormat = expectedFormat;
            return this;
        }
        
        public Builder withValidationType(ValidationType validationType) {
            this.validationType = validationType;
            return this;
        }
        
        public Builder addViolation(ConstraintViolation violation) {
            this.violations.add(violation);
            return this;
        }
        
        public Builder addViolation(String fieldName, Object actualValue, String message) {
            this.violations.add(new ConstraintViolation(fieldName, actualValue, message));
            return this;
        }
        
        public Builder addValidationContext(String key, Object value) {
            this.validationContext.put(key, value);
            return this;
        }
        
        public ValidationException build() {
            return new ValidationException(this);
        }
    }
    
    /**
     * Represents a single constraint violation
     */
    public static class ConstraintViolation {
        private final String fieldName;
        private final Object actualValue;
        private final String message;
        private final String constraintType;
        
        public ConstraintViolation(String fieldName, Object actualValue, String message) {
            this(fieldName, actualValue, message, null);
        }
        
        public ConstraintViolation(String fieldName, Object actualValue, String message, String constraintType) {
            this.fieldName = fieldName;
            this.actualValue = actualValue;
            this.message = message;
            this.constraintType = constraintType;
        }
        
        public String getFieldName() {
            return fieldName;
        }
        
        public Object getActualValue() {
            return actualValue;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getConstraintType() {
            return constraintType;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(fieldName).append(": ").append(message);
            if (actualValue != null) {
                sb.append(" (actual value: ").append(actualValue).append(")");
            }
            return sb.toString();
        }
    }
    
    /**
     * Types of validation
     */
    public enum ValidationType {
        GENERAL,            // General validation
        FIELD,              // Single field validation
        REQUIRED,           // Required field validation
        URL,                // URL validation
        HTTP_METHOD,        // HTTP method validation
        RANGE,              // Range validation
        LENGTH,             // Length validation
        FORMAT,             // Format validation
        PATTERN,            // Pattern/regex validation
        AUTHENTICATION,     // Authentication validation
        AUTHORIZATION,      // Authorization validation
        HEADER,             // Header validation
        BODY,               // Body validation
        MULTIPLE            // Multiple violations
    }
}
