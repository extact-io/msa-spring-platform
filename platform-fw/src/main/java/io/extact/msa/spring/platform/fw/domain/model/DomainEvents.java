package io.extact.msa.spring.platform.fw.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class DomainEvents {

    private List<DomainEvent> domainEvents = new ArrayList<>();

    public void add(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }
}
