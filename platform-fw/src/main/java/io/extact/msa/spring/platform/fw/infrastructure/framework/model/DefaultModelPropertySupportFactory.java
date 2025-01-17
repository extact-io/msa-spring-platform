package io.extact.msa.spring.platform.fw.infrastructure.framework.model;

import java.util.function.Supplier;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupport;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultModelPropertySupportFactory implements ModelPropertySupportFactory {

    private final ModelValidator validator;

    public ModelPropertySupport create(Supplier<EntityModel> testModelCreator, EntityModel updateModel) {
        return new DefaultModelPropertySupport(testModelCreator, validator, updateModel);
    }
}
