package io.extact.msa.spring.platform.core.debug;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.core.utils.LoggingUtils;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ConfigDumpProperties.class)
@EnableAspectJAutoProxy
public class DebugConfig {

    @Bean
    @ConditionalOnProperty(prefix = "rms.debug.configdump", name = "enable", havingValue = "true")
    ConfigDump configDump(Environment env, ConfigDumpProperties dumpProps, LoggingSystem loggingSystem) {
        LoggingUtils.forceLogEnable(loggingSystem, "ConfigDump", LogLevel.DEBUG);
        return new ConfigDump(env, dumpProps);
    }

    @Bean
    @ConditionalOnProperty(prefix = "rms.debug.sleep", name = "enable", havingValue = "true")
    DebugSleepInterceptor debugSleepInterceptor() {
        return new DebugSleepInterceptor();
    }
}
