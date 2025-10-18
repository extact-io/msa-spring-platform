package io.extact.msa.spring.platform.core.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import ch.qos.logback.access.tomcat.LogbackValve;
import io.extact.msa.spring.platform.core.log.access.ServletAccessLoggingFilter;
import io.extact.msa.spring.platform.core.utils.LoggingUtils;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;

@Configuration(proxyBeanMethods = false)
public class LogConfig {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnProperty(prefix = "rms.log.access", name = "enable", havingValue = "true")
    static class AccessLogConfig {

        @Bean
        @ConditionalOnClass(LogbackValve.class)
        @ConditionalOnResource(resources = LogbackValve.DEFAULT_FILENAME)
        @ConditionalOnProperty(prefix = "rms.log.access", name = "type", havingValue = "logback")
        LogbackAccessConfigCustomizer logbackAccessConfigCustomizer() {
            return new LogbackAccessConfigCustomizer();
        }

        @Bean
        @ConditionalOnProperty(prefix = "rms.log.access", name = "type", havingValue = "spring")
        FilterRegistrationBean<CommonsRequestLoggingFilter> commonsRequestLoggingFilter(LoggingSystem loggingSystem) {

            CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
            String loggerName = filter.getClass().getName();
            LoggingUtils.forceLogEnable(loggingSystem, loggerName, LogLevel.DEBUG);

            filter.setIncludeQueryString(true);
            filter.setIncludePayload(false);
            //filter.setMaxPayloadLength(10000);
            filter.setIncludeHeaders(true);
            filter.setBeforeMessagePrefix("[ACCESS:befor] ");
            filter.setAfterMessagePrefix("[ACCESS:after] ");

            FilterRegistrationBean<CommonsRequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
            registrationBean.setFilter(filter);
            registrationBean.setOrder(-200); // Spring Securityより優先させる
            return registrationBean;
        }

        @Bean
        @ConditionalOnClass(LogbackValve.class)
        @ConditionalOnProperty(prefix = "rms.log.access", name = "type", havingValue = "rms")
        FilterRegistrationBean<ServletAccessLoggingFilter> servletAccessLoggingFilter(LoggingSystem loggingSystem,
                Environment env) {

            ServletAccessLoggingFilter filter = new ServletAccessLoggingFilter(env);
            String loggerName = filter.getClass().getName();
            LoggingUtils.forceLogEnable(loggingSystem, loggerName, LogLevel.DEBUG);

            FilterRegistrationBean<ServletAccessLoggingFilter> registrationBean = new FilterRegistrationBean<>();
            registrationBean.setFilter(filter);
            registrationBean.setOrder(-300); // Spring Securityより優先させる
            return registrationBean;
        }
    }

    @Bean
    @ConditionalOnClass(OpenTelemetryAppender.class)
    @ConditionalOnProperty(name = "management.otlp.logging.export.enabled", havingValue = "true")
    OpenTelemetryAppenderInitializer appenderInitializer(OpenTelemetry openTelemetry) {
        return new OpenTelemetryAppenderInitializer(openTelemetry);
    }
}
