package io.extact.msa.spring.platform.core.debug;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.core.CoreConfiguration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ConfigDumpProperties.class)
@EnableAspectJAutoProxy
@Import(CoreConfiguration.class)
public class DebugConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "rms.debug.configdump", name = "enable", havingValue = "true")
    ConfigDump configDump(Environment env, ConfigDumpProperties dumpProps) {
        return new ConfigDump(env, dumpProps);
    }

    @Bean
    @ConditionalOnProperty(prefix = "rms.debug.sleep", name = "enable", havingValue = "true")
    DebugSleepInterceptor debugSleepInterceptor() {
        return new DebugSleepInterceptor();
    }
}
