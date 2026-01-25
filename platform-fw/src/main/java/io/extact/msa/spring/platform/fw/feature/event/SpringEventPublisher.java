package io.extact.msa.spring.platform.fw.feature.event;

import org.springframework.context.ApplicationEventPublisher;

import io.extact.msa.spring.platform.fw.application.event.ApplicationServiceEvent;
import io.extact.msa.spring.platform.fw.application.event.ApplicationServiceEventPublisher;
import io.extact.msa.spring.platform.fw.domain.model.DomainEvent;
import io.extact.msa.spring.platform.fw.domain.service.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringEventPublisher implements DomainEventPublisher, ApplicationServiceEventPublisher {

    private final ApplicationEventPublisher springEventPublisher;

    @Override
    public void publish(ApplicationServiceEvent event) {
        springEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(DomainEvent event) {
        springEventPublisher.publishEvent(event);
    }
}
