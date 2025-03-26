package io.extact.msa.spring.platform.fw.feature.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class EventPublisherConfig {

    @Bean
    SpringEventPublisher springEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringEventPublisher(publisher);
    }
}
