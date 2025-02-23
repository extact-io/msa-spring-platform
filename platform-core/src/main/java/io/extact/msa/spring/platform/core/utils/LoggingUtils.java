package io.extact.msa.spring.platform.core.utils;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingUtils {

    public static boolean isLogEnabled(LoggingSystem loggingSystem, String loggerName, LogLevel level) {
        LoggerConfiguration config = loggingSystem.getLoggerConfiguration(loggerName);
        if (config == null) {
            return false;
        }
        return config.getEffectiveLevel().ordinal() <= level.ordinal();
    }

    public static void forceLogEnable(LoggingSystem loggingSystem, String loggerName, LogLevel level) {
        if (!LoggingUtils.isLogEnabled(loggingSystem, loggerName, level)) {
            log.info("Force enabled {} log level on {}", level, loggerName);
            loggingSystem.setLogLevel(loggerName, level);
        }
    }

}
