package io.extact.msa.spring.platform.fw.persistence.file;

import io.extact.msa.spring.platform.fw.domain.DomainModel;

public interface ModelArrayMapper<M extends DomainModel> {

    M toModel(String[] attributes);

    String[] toArray(M entity);
}
