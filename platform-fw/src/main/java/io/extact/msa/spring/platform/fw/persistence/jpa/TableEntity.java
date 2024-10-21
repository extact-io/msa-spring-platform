package io.extact.msa.spring.platform.fw.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.DomainModel;

public interface TableEntity<M extends DomainModel> {

    default Integer getPk() {
        return getId();
    }

    Integer getId();

    M toModel();
}
