package io.extact.msa.spring.test.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;

import lombok.RequiredArgsConstructor;

/**
 * OutputCaptureExtensionを使ったテストをする際に一時的にログの
 * 出力量を強制的に減らしたい場合に利用する。
 * 実装は{@link LoggingSystem}の実装を参考にしている。
 * @see org.springframework.boot.logging.LoggingSystem
 */
@RequiredArgsConstructor
public class RestorableLoggingSuppressor {

    private final LoggingSystem loggingSystem;
    private Map<String, LogLevel> originalLevels = new HashMap<>();

    /**
     * すべてのロガーの出力を抑止する
     * @param excludingLoggerNames 抑止しないロガー名
     * @param excludingLevel 抑止しないロガーに設定するログレベル
     * @param mutedLevel ミュートするログレベル
     */
    public void suppressAllExcluding(List<String> excludingLoggerNames, LogLevel excludingLevel, LogLevel mutedLevel) {

        for (LoggerConfiguration logger : loggingSystem.getLoggerConfigurations()) {
            String loggerName = logger.getName();
            LogLevel before = logger.getConfiguredLevel();

            originalLevels.put(loggerName, before);

            if (excludingLoggerNames.stream().anyMatch(loggerName::startsWith)) {
                loggingSystem.setLogLevel(loggerName, excludingLevel);
            } else {
                loggingSystem.setLogLevel(loggerName, mutedLevel);
            }
        }
    }

    public void suppressAllExcluding(String excludingLoggerNames, LogLevel excludingLevel, LogLevel mutedLevel) {
        suppressAllExcluding(List.of(excludingLoggerNames), excludingLevel, mutedLevel);
    }

    /**
     * すべてのロガーの出力を抑止する
     * @param mutedLevel ミュートするログレベル
     */
    public void suppressAll(LogLevel mutedLevel) {
        this.suppressAllExcluding(Collections.emptyList(), null, mutedLevel);
    }

    public void restoreLogLevel() {
        originalLevels.entrySet().forEach(entry -> {
            String loggerName = entry.getKey();
            LogLevel originalLevel = entry.getValue();
            loggingSystem.setLogLevel(loggerName, originalLevel);
        });
    }
}
