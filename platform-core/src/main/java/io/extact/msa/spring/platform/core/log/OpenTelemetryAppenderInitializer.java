package io.extact.msa.spring.platform.core.log;

import jakarta.annotation.PostConstruct;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;

/**
 * OpenTelemetryAppenderの初期化クラス。
 * 初期化を行うイベント契機(@PostConstruct)を得るためBeanで被せている
 */
class OpenTelemetryAppenderInitializer {

    private final OpenTelemetry openTelemetry;

    OpenTelemetryAppenderInitializer(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @PostConstruct
    void init() {
        OpenTelemetryAppender.install(this.openTelemetry);
    }
}
