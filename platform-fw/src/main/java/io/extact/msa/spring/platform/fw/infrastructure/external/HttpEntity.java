package io.extact.msa.spring.platform.fw.infrastructure.external;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;

public interface HttpEntity<M extends DomainModel> {

    M toModel();
}
