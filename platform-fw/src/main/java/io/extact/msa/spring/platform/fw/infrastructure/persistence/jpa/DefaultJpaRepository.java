package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.infrastructure.ModelEntityMapper;

public class DefaultJpaRepository<M extends DomainModel, E extends TableEntity<M>>
    extends AbstractJpaRepository<M, E> implements GenericRepository<M> {

    public DefaultJpaRepository(JpaRepositoryDelegator<E> executor, ModelEntityMapper<M, E> modelEntityMapper,
            SequenceGeneratorFactory sequencefactory) {
        super(executor, modelEntityMapper, sequencefactory);
    }

    public DefaultJpaRepository(JpaRepositoryDelegator<E> executor, ModelEntityMapper<M, E> modelEntityMapper) {
        super(executor, modelEntityMapper);
    }
}
