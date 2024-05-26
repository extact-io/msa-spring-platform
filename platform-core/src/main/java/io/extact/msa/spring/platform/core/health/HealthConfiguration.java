package io.extact.msa.spring.platform.core.health;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import io.extact.msa.spring.platform.core.CoreConfiguration;
import io.extact.msa.spring.platform.core.async.AsyncInvoker;
import io.extact.msa.spring.platform.core.condition.ConditionalOnPropertyList;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClientFactory;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClientFactoryImpl;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DependentServersHealthIndicator.Properties.class)
@EnableAsync
@ConditionalOnPropertyList(key = "rms.health.depend-services")
@Import(CoreConfiguration.class)
public class HealthConfiguration {

    @Bean
    AsyncInvoker asyncInvoker() {
        return new AsyncInvoker();
    }

    @Bean
    ReadinessProbeRestClientFactory readinessProbeRestClientFactory(AsyncInvoker asyncInvoker) {
        return new ReadinessProbeRestClientFactoryImpl(asyncInvoker);
    }

    @Bean
    DependentServersHealthIndicator dependentServersHealthIndicator(ReadinessProbeRestClientFactory factory,
            DependentServersHealthIndicator.Properties properties) {
        return new DependentServersHealthIndicator(factory, properties);
    }
}
