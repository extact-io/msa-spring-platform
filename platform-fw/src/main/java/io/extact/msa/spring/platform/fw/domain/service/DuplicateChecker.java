package io.extact.msa.spring.platform.fw.domain.service;

public interface DuplicateChecker<M> {
    public void check(M checkModel);
}
