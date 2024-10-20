package io.extact.msa.spring.platform.fw.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.DomainModel;

public interface TableEntity<M extends DomainModel> {

    M toModel();
}
