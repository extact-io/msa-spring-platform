package io.extact.msa.spring.platform.fw.domain.model;

import jakarta.validation.Validator;

public interface ValidatorAware {
    void configureValidator(Validator validator);
}
