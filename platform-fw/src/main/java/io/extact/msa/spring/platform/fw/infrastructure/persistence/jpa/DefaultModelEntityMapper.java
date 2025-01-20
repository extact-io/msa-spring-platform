package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import java.util.function.Function;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.ModelEntityMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultModelEntityMapper<M extends EntityModel, E extends TableEntity<M>>
        implements ModelEntityMapper<M, E> {

    private final Function<M, E> modelToEntityMapper;
    private final ModelPropertySupportFactory modelSupportFactory;

    @Override
    public M toModel(E entity) {
        return entity.toModel(modelSupportFactory);
    }

    @Override
    public E toEnity(M model) {
        return model.transform(modelToEntityMapper);
    }
}
