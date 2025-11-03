package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.IdCreator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.PhysicalEntity;

public class DefaultJpaRepository<M extends EntityModel, I extends Identity, E extends PhysicalEntity<M>>
        extends AbstractJpaRepository<M, I, E> implements GenericRepository<M> {

    public DefaultJpaRepository(
            JpaRepositoryDelegator<E> executor,
            ModelEntityMapper<M, E> modelEntityMapper,
            SequenceGeneratorFactory sequencefactory,
            IdCreator<I> idCreator) {

        super(executor, modelEntityMapper, sequencefactory, idCreator);
    }

    public DefaultJpaRepository(
            JpaRepositoryDelegator<E> executor,
            ModelEntityMapper<M, E> modelEntityMapper,
            IdCreator<I> idCreator) {

        super(executor, modelEntityMapper, idCreator);
    }
}
