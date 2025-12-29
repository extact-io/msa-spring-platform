package io.extact.msa.spring.test.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestorableLoggingSilencer {

    private final LoggingSystem loggingSystem;
    private Map<String, LogLevel> originalLevels = new HashMap<>();

    public void muteLogLevel(String keepLoggerName, LogLevel keepLevel, LogLevel mutedLevel) {
        muteLogLevel(List.of(keepLoggerName), keepLevel, mutedLevel);
    }

    public void muteLogLevel(List<String> keepLoggerNames, LogLevel keepLevel, LogLevel mutedLevel) {

        for (LoggerConfiguration logger : loggingSystem.getLoggerConfigurations()) {
            String loggerName = logger.getName();
            LogLevel before = logger.getConfiguredLevel();

            originalLevels.put(loggerName, before);

            if (keepLoggerNames.stream().anyMatch(loggerName::startsWith)) {
                loggingSystem.setLogLevel(loggerName, keepLevel);
            } else {
                loggingSystem.setLogLevel(loggerName, mutedLevel);
            }
        }
    }

    public void restoreLogLevel() {
        originalLevels.entrySet().forEach(entry -> {
            String loggerName = entry.getKey();
            LogLevel originalLevel = entry.getValue();
            loggingSystem.setLogLevel(loggerName, originalLevel);
        });
    }
}
