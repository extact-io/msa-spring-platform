package io.extact.msa.spring.platform.fw.domain.model;

import java.util.function.Supplier;

public interface ModelPropertySupportFactory<M extends DomainModel> {

    ModelPropertySupport create(Supplier<M> testModelCreator, M updateModel);
}
