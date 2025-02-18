package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;

public interface TableEntity<M extends EntityModel> {

    default Integer getPk() {
        return getId();
    }

    Integer getId();

    M toModel(ModelValidator validator);
}
