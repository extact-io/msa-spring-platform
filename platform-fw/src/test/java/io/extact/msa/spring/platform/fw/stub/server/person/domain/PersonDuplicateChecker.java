package io.extact.msa.spring.platform.fw.stub.server.person.domain;

import java.util.function.Predicate;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonDuplicateChecker {

    private final PersonRepository repository;

    public void check(Person person) {
        repository.findName(person.getName())
                .filter(Predicate.not(person::equals))
                .ifPresent(match -> {
                    throw new BusinessFlowException("The name is already registered.", CauseType.DUPLICATE);
                });
    }
}
