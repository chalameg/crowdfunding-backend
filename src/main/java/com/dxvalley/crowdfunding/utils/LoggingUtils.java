package com.dxvalley.crowdfunding.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUtils {
    public static void logError(Class<?> clazz, String methodName, Exception ex) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error("An error occurred in {}.{}. Details: {}", clazz.getSimpleName(), methodName, ex.getMessage(), ex);
    }
}
