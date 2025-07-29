package com.t24.apiproxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUtil {
    public static void init() {
        assert LoggerFactory.getILoggerFactory() != null : "LoggerFactory is not initialized";
        Logger logger = LoggerFactory.getLogger(LoggingUtil.class);
        logger.info("LoggingUtil initialized successfully."); 
    }

    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }
}
