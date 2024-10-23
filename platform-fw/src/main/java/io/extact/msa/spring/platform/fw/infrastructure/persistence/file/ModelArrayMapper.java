package io.extact.msa.spring.platform.fw.infrastructure.persistence.file;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;

public interface ModelArrayMapper<M extends DomainModel> {

    M toModel(String[] attributes);

    String[] toArray(M entity);
}
