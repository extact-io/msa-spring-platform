package io.extact.msa.spring.platform.fw.feature.observation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObservedAspect.class)
@ConditionalOnProperty(name = "env.otlp.enabled", havingValue = "true")
public class ObservationConfig {

    /* -- NOTICE --
     * Aspectを登録する場合はclasspathにspring-boot-starter-aopを含めること
     */
    @Bean
    LayerObservationAwareAspect layerObservationAwareAspect(ObservationRegistry observationRegistry) {
        return new LayerObservationAwareAspect(observationRegistry);
    }
}
