package io.extact.msa.spring.platform.fw.domain.model;

public interface EntityModel extends DomainModel, Identifiable, Comparable<EntityModel>  {

    @Override
    default int compareTo(EntityModel other) {
        return getId().compareTo(other.getId());
    }

    void configure(ModelValidator validator);

    void verify();
}
