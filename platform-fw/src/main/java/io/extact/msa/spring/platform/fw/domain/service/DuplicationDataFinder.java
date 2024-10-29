package io.extact.msa.spring.platform.fw.domain.service;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;

public interface DuplicationDataFinder<M extends DomainModel> {
    Optional<M> findDuplicationData(M checkModel);
}
