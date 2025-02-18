package io.extact.msa.spring.platform.fw.domain.model;

public interface ModelValidatorAware {
    void configure(ModelValidator validator);
}
