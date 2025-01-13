package io.extact.msa.spring.platform.fw.infrastructure.external;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;

public interface HttpEntity<M extends EntityModel> {

    M toModel();
}
