package io.extact.msa.spring.platform.fw.domain.model;

import lombok.ToString;

public abstract class AbstractEntityModel implements EntityModel {

    @ToString.Exclude
    private ModelValidator validator;

    @Override
    public void configure(ModelValidator validator) {
        this.validator = validator;
    }

    @Override
    public void verify() {
        validator.validateModel(this);
    }
    public void verify(Object... groups) {
        validator.validateModel(this, groups);
    }

    protected ModelValidator validator() {
        return validator;
    }
}
