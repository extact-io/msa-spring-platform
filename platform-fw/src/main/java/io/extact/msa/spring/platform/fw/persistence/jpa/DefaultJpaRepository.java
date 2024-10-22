package io.extact.msa.spring.platform.fw.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;

public class DefaultJpaRepository<M extends DomainModel, E extends TableEntity<M>>
    extends AbstractJpaRepository<M, E> implements GenericRepository<M> {

    public DefaultJpaRepository(SpringDataJpaExecutor<E> executor, ModelEntityMapper<M, E> modelEntityMapper,
            SequenceGeneratorFactory sequencefactory) {
        super(executor, modelEntityMapper, sequencefactory);
    }

    public DefaultJpaRepository(SpringDataJpaExecutor<E> executor, ModelEntityMapper<M, E> modelEntityMapper) {
        super(executor, modelEntityMapper);
    }

}
