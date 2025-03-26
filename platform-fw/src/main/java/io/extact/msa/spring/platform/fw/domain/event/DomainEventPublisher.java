package io.extact.msa.spring.platform.fw.domain.event;

import java.util.List;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

    default void publish(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
