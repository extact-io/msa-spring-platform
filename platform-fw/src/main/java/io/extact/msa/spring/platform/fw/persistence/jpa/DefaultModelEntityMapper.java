package io.extact.msa.spring.platform.fw.persistence.jpa;

import java.util.function.Function;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultModelEntityMapper<M extends DomainModel, E extends TableEntity<M>>
        implements ModelEntityMapper<M, E> {

    private final Function<M, E> modelToEntityMapper;

    @Override
    public M toModel(E entity) {
        return entity.toModel();
    }

    @Override
    public E toEntity(M model) {
        return model.transform(modelToEntityMapper);
    }
}
