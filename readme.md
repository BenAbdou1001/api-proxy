# API Proxy JAR - Full Architecture Design

## 1. Project Overview

**Purpose**: A JAR program that processes various input formats (CSV, JSON, text, params) containing API request information, executes those requests, and returns responses. Designed for T24 Temenos integration.

**Key Features**:
- Multi-format input processing (CSV, JSON, text, parameters)
- Dynamic API request execution
- Comprehensive error handling
- T24 Temenos integration ready
- Enterprise-grade logging and monitoring

## 2. High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    T24 TEMENOS SYSTEM                       │
│  ┌─────────────────────────────────────────────────────────┐│
│  │              COB/EOD Process                             ││
│  │                     │                                   ││
│  │                     ▼                                   ││
│  │  ┌─────────────────────────────────────────────────────┐││
│  │  │            API Proxy JAR                            │││
│  │  │                                                     │││
│  │  │  ┌─────────────────────────────────────────────────┐│││
│  │  │  │           Input Processor                       ││││
│  │  │  │  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────────────────┐ ││││
│  │  │  │  │ CSV │  │JSON │  │TEXT │  │   Parameters    │ ││││
│  │  │  │  └─────┘  └─────┘  └─────┘  └─────────────────┘ ││││
│  │  │  └─────────────────────────────────────────────────┘│││
│  │  │                     │                               │││
│  │  │                     ▼                               │││
│  │  │  ┌─────────────────────────────────────────────────┐│││
│  │  │  │         Request Builder & Validator             ││││
│  │  │  └─────────────────────────────────────────────────┘│││
│  │  │                     │                               │││
│  │  │                     ▼                               │││
│  │  │  ┌─────────────────────────────────────────────────┐│││
│  │  │  │           HTTP Client Manager                   ││││
│  │  │  └─────────────────────────────────────────────────┘│││
│  │  │                     │                               │││
│  │  │                     ▼                               │││
│  │  │  ┌─────────────────────────────────────────────────┐│││
│  │  │  │          Response Processor                     ││││
│  │  │  └─────────────────────────────────────────────────┘│││
│  │  └─────────────────────────────────────────────────────┘││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   External APIs                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   REST API  │  │  SOAP API   │  │    GraphQL API      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 3. Package Structure

```
com.t24.apiproxy
├── main/
│   ├── ApiProxyMain.java
│   └── config/
│       ├── Configuration.java
│       └── ConfigurationLoader.java
├── input/
│   ├── InputProcessor.java
│   ├── parsers/
│   │   ├── CsvParser.java
│   │   ├── JsonParser.java
│   │   ├── TextParser.java
│   │   └── ParameterParser.java
│   └── validation/
│       └── InputValidator.java
├── model/
│   ├── ApiRequest.java
│   ├── ApiResponse.java
│   ├── RequestMetadata.java
│   └── ErrorResponse.java
├── client/
│   ├── HttpClientManager.java
│   ├── RequestBuilder.java
│   ├── ResponseProcessor.java
│   └── adapters/
│       ├── RestAdapter.java
│       ├── SoapAdapter.java
│       └── GraphQLAdapter.java
├── security/
│   └── SSLUtil.java
├── util/
│   ├── LoggingUtil.java
│   ├── DateUtil.java
│   ├── StringUtil.java
│   └── ValidationUtil.java
└── exception/
    ├── ApiProxyException.java
    ├── InputProcessingException.java
    ├── NetworkException.java
    └── ValidationException.java
```

## 4. Input Format Specifications

### 4.1 CSV Format
```csv
url,method,headers,body,timeout
https://api.example.com/users,GET,Content-Type:application/json,null,30000
https://api.example.com/users,POST,Content-Type:application/json,"{\"name\":\"John\"}",30000
```

### 4.2 JSON Format
```json
[
  {
    "url": "https://api.example.com/users",
    "method": "GET",
    "headers": {
      "Content-Type": "application/json",
      "Accept": "application/json"
    },
    "body": null,
    "timeout": 30000
  }
]
```

### 4.3 Text Format
```text
https://api.example.com/users,GET,Content-Type:application/json,null,30000
https://api.example.com/data,POST,Content-Type:application/json,{"key":"value"},30000
```

### 4.4 Parameter Format
Direct method parameters or properties-style configuration.


## 5. Configuration Management

### 5.1 Configuration File (application.properties)
```properties
# HTTP Client Configuration
http.connection.timeout=30000
http.socket.timeout=60000
http.max.connections=100
http.connection.request.timeout=30000

# SSL Configuration
ssl.trust.all.certificates=false
ssl.keystore.path=/path/to/keystore
ssl.keystore.password=password
ssl.truststore.path=/path/to/truststore
ssl.truststore.password=password

# Proxy Configuration
proxy.enabled=false
proxy.host=proxy.company.com
proxy.port=8080

# Logging Configuration
logging.level=INFO
logging.file.path=/logs/apiproxy
logging.max.file.size=10MB
logging.max.files=10

# Security
encryption.key=your-encryption-key
default.auth.timeout=3600
```

## 6. Error Handling Strategy

### 6.1 Exception Hierarchy
```java
public class ApiProxyException extends Exception {
    private String errorCode;
    private String userMessage;
    private String technicalMessage;
}

public class InputProcessingException extends ApiProxyException {}
public class NetworkException extends ApiProxyException {}
public class ValidationException extends ApiProxyException {}
public class SecurityException extends ApiProxyException {}
```

### 6.2 Error Response Format
```json
{
  "success": false,
  "errorCode": "INPUT_VALIDATION_ERROR",
  "userMessage": "Invalid input format detected",
  "technicalMessage": "CSV parsing failed at line 3: missing required column 'url'",
  "timestamp": "2024-01-20T10:30:00Z",
  "executionId": "exec_20240120_001"
}
```

## 7. Logging and Monitoring

### 7.1 Logging Strategy
- **INFO**: Successful operations, execution summaries
- **WARN**: Recoverable errors, fallback mechanisms used
- **ERROR**: Failed operations, exceptions
- **DEBUG**: Detailed execution flow, request/response details

### 7.2 Log Format
```
[2024-01-20 10:30:00.123] [INFO] [exec_001] [HttpClientManager] Executing GET request to https://api.example.com/users
[2024-01-20 10:30:00.580] [INFO] [exec_001] [HttpClientManager] Request completed successfully. Status: 200, Time: 457ms
```

## 8. Performance Considerations

### 8.1 Optimization Features
- **Connection Pooling**: Reuse HTTP connections
- **Parallel Execution**: Process multiple requests concurrently
- **Caching**: Cache responses when appropriate
- **Timeout Management**: Prevent hanging requests
- **Memory Management**: Efficient object lifecycle

### 8.2 Threading Strategy
```java
public class ParallelRequestProcessor {
    private ExecutorService executorService;
    private CompletionService<ApiResponse> completionService;
    
    public List<ApiResponse> processRequestsParallel(List<ApiRequest> requests) {
        // Parallel execution with configurable thread pool
    }
}
```

## 9. Security Features

### 9.1 Security Checklist
- ✅ SSL/TLS support with certificate validation
- ✅ Multiple authentication methods (Basic, Bearer, API Key, OAuth2)
- ✅ Sensitive data encryption
- ✅ Input validation and sanitization
- ✅ Request/response logging (with sensitive data masking)
- ✅ Configurable timeouts to prevent DoS
- ✅ Proxy support for corporate environments

### 9.2 Data Protection
- Encrypt authentication credentials in configuration
- Mask sensitive data in logs
- Secure temporary file handling
- Memory cleanup for sensitive operations