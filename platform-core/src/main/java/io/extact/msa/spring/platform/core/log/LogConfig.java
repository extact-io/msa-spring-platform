package io.extact.msa.spring.platform.core.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import ch.qos.logback.access.tomcat.LogbackValve;
import io.extact.msa.spring.platform.core.utils.LoggingUtils;

@Configuration(proxyBeanMethods = false)
public class LogConfig {

    @Bean
    @ConditionalOnClass(LogbackValve.class)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnResource(resources = LogbackValve.DEFAULT_FILENAME)
    LogbackAccessConfigCustomizer logbackAccessConfigCustomizer() {
        return new LogbackAccessConfigCustomizer();
    }

    @Bean
    @ConditionalOnProperty(prefix = "rms.log.server", name = "enable", havingValue = "true")
    CommonsRequestLoggingFilter logFilter(LoggingSystem loggingSystem) {

        String loggerName = CommonsRequestLoggingFilter.class.getName();
        LoggingUtils.forceLogEnable(loggingSystem, loggerName, LogLevel.DEBUG);

        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");

        return filter;
    }
}
