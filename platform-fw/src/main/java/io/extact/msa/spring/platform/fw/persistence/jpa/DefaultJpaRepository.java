package io.extact.msa.spring.platform.fw.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.DomainModel;

public class DefaultJpaRepository<M extends DomainModel, E extends TableEntity<M>> extends AbstractJpaRepository<M, E> {

    public DefaultJpaRepository(SpringDataJpaExecutor<E> executor, ModelEntityMapper<M, E> modelEntityMapper) {
        super(executor, modelEntityMapper);
    }
}
