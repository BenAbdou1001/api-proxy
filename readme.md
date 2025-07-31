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


## 5. Logging and Monitoring

### 5.1 Logging Strategy
- **INFO**: Successful operations, execution summaries
- **WARN**: Recoverable errors, fallback mechanisms used
- **ERROR**: Failed operations, exceptions
- **DEBUG**: Detailed execution flow, request/response details

### 5.2 Log Format
```
[2024-01-20 10:30:00.123] [INFO] [exec_001] [HttpClientManager] Executing GET request to https://api.example.com/users
[2024-01-20 10:30:00.580] [INFO] [exec_001] [HttpClientManager] Request completed successfully. Status: 200, Time: 457ms
```

## 6. Performance Considerations
### Optimization Features
- **Connection Pooling**: Reuse HTTP connections
- **Parallel Execution**: Process multiple requests concurrently
- **Caching**: Cache responses when appropriate
- **Timeout Management**: Prevent hanging requests
- **Memory Management**: Efficient object lifecycle


## 7. Security Features

### 7.1 Security Checklist
- ✅ SSL/TLS support with certificate validation
- ✅ Multiple authentication methods (Basic, Bearer, API Key, OAuth2)
- ✅ Sensitive data encryption
- ✅ Input validation and sanitization
- ✅ Request/response logging (with sensitive data masking)
- ✅ Configurable timeouts to prevent DoS
- ✅ Proxy support for corporate environments

### 7.2 Data Protection
- Encrypt authentication credentials in configuration
- Mask sensitive data in logs
- Secure temporary file handling
- Memory cleanup for sensitive operations
