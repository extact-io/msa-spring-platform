package io.extact.msa.spring.platform.fw.domain.service;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;

public interface DuplicationDataFinder<M extends EntityModel> extends DomainService {
    Optional<M> findDuplicationData(M checkModel);
}
