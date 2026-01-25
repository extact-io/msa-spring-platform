package io.extact.msa.spring.platform.fw.domain.model;

import java.util.List;

public interface DomainEventStorable {
    List<DomainEvent> pullDomainEvents();
}
