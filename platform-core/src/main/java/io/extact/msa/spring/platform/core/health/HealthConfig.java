package io.extact.msa.spring.platform.core.health;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

import io.extact.msa.spring.platform.core.async.AsyncConfig;
import io.extact.msa.spring.platform.core.async.AsyncInvoker;
import io.extact.msa.spring.platform.core.condition.ConditionalOnPropertyList;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClientFactory;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClientFactoryImpl;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DependentServersHealthIndicator.Properties.class)
@ConditionalOnPropertyList(key = "rms.health.depend-services")
@Import(AsyncConfig.class)
public class HealthConfig {

    @Bean
    ReadinessProbeRestClientFactory readinessProbeRestClientFactory(AsyncInvoker asyncInvoker,
            RestClient.Builder builder) {
        return new ReadinessProbeRestClientFactoryImpl(asyncInvoker, builder);
    }

    @Bean
    DependentServersHealthIndicator dependentServersHealthIndicator(ReadinessProbeRestClientFactory factory,
            DependentServersHealthIndicator.Properties properties) {
        return new DependentServersHealthIndicator(factory, properties);
    }
}
