package io.extact.msa.spring.platform.fw.domain.service;

public interface DuplicateChecker<M> extends DomainService {
    void check(M checkModel);
}
