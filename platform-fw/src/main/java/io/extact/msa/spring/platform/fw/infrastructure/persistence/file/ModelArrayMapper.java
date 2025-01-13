package io.extact.msa.spring.platform.fw.infrastructure.persistence.file;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;

public interface ModelArrayMapper<M extends EntityModel> {

    M toModel(String[] attributes);

    String[] toArray(M entity);
}
