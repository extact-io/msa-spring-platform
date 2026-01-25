package io.extact.msa.spring.platform.fw.domain.service;

import java.util.List;

import io.extact.msa.spring.platform.fw.domain.model.DomainEvent;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

    default void publish(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
