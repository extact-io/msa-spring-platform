package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;

public interface TableEntity<M extends DomainModel> {

    default Integer getPk() {
        return getId();
    }

    Integer getId();

    M toModel();
}
