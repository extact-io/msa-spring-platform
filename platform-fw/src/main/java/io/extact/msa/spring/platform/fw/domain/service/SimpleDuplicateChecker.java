package io.extact.msa.spring.platform.fw.domain.service;

import java.util.function.Predicate;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.repository.DuplicationDataFinder;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleDuplicateChecker<M extends EntityModel> implements DuplicateChecker<M> {

    private final DuplicationDataFinder<M> repository;

    @Override
    public void check(M checkModel) {
        repository.findDuplicationData(checkModel)
                .filter(Predicate.not(checkModel::equals))
                .ifPresent(_ -> {
                    throw new BusinessFlowException("The name is already registered.", CauseType.DUPLICATE);
                });
    }
}
