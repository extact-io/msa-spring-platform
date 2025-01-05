package io.extact.msa.spring.platform.fw.domain.model;

import jakarta.validation.groups.Default;

public interface ModelValidator<M> {

    void validateModel(M model, Object... groups);

    default void validateModel(M model) {
        this.validateModel(model, Default.class);
    }

    void validateField(M model, String targetField, Object... groups);

    default void validateField(M model, String targetField) {
        this.validateField(model, targetField, Default.class);
    }
}
