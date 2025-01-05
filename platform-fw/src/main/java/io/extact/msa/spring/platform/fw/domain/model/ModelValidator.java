package io.extact.msa.spring.platform.fw.domain.model;

import jakarta.validation.groups.Default;

public interface ModelValidator {

    void validateModel(DomainModel model, Object... groups);

    default void validateModel(DomainModel model) {
        this.validateModel(model, Default.class);
    }

    void validateField(DomainModel model, String targetField, Object... groups);

    default void validateField(DomainModel model, String targetField) {
        this.validateField(model, targetField, Default.class);
    }
}
