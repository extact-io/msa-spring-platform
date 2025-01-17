package io.extact.msa.spring.platform.fw.domain.model;

import java.util.function.Supplier;

public interface ModelPropertySupportFactory {

    ModelPropertySupport create(Supplier<EntityModel> testModelCreator, EntityModel updateModel);
}
