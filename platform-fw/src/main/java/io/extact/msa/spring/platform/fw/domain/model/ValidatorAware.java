package io.extact.msa.spring.platform.fw.domain.model;

public interface ValidatorAware<M> {
    void configureValidator(ModelValidator<M> validator);
}
