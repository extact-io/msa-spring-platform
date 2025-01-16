package io.extact.msa.spring.platform.fw.infrastructure.framework.model;

import java.util.function.Supplier;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupport;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultModelPropertySupportFactory<M extends DomainModel> implements ModelPropertySupportFactory<M> {

    private final ModelValidator validator;

    public ModelPropertySupport create(Supplier<M> testModelCreator, M updateModel) {
        return new DefaultModelSetterSupport<>(testModelCreator, validator, updateModel);
    }
}
