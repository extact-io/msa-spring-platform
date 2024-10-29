package io.extact.msa.spring.platform.fw.domain.service;

import java.util.function.Predicate;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleDuplicateChecker<M extends DomainModel> implements DuplicateChecker<M> {

    private final DuplicationDataFinder<M> repository;

    @Override
    public void check(M checkModel) {
        repository.findDuplicationData(checkModel)
                .filter(Predicate.not(checkModel::equals))
                .ifPresent(match -> {
                    throw new BusinessFlowException("The name is already registered.", CauseType.DUPLICATE);
                });
    }
}
