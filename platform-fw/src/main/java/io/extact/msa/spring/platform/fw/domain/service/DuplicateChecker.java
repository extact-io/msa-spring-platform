package io.extact.msa.spring.platform.fw.domain.service;

public interface DuplicateChecker<M> {
    void check(M checkModel);
}
