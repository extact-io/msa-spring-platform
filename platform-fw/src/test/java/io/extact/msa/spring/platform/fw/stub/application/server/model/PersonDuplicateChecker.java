package io.extact.msa.spring.platform.fw.stub.application.server.model;

import java.util.function.Predicate;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonDuplicateChecker {

    private PersonRepository repository;

    public void check(Person person) {
        repository.findName(person.getName())
                .filter(Predicate.not(person::equals))
                .ifPresent(match -> {
                    throw new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND);
                });
    }
}
