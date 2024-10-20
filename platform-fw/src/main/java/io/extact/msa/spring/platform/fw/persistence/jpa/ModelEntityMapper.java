package io.extact.msa.spring.platform.fw.persistence.jpa;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.Value;

public interface ModelEntityMapper<M, E> {
    M toModel(E entity);
    E toEntity(M model);

    @Value
    @RequiredArgsConstructor
    static class DefaultModelEntityConveter<M, E> implements ModelEntityMapper<M, E> {

        private final Function<M, E> modelToEntityMapper;
        private final Function<E, M> entityToModelMapper;

        @Override
        public M toModel(E entity) {
            return entityToModelMapper.apply(entity);
        }

        @Override
        public E toEntity(M model) {
            return modelToEntityMapper.apply(model);
        }
    }
}
