package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.service.IdentityGenerator;
import io.extact.msa.spring.platform.fw.feature.exception.RmsPersistenceException;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.PhysicalEntity;

public abstract class AbstractJpaRepository<M extends EntityModel, E extends PhysicalEntity<M>>
        implements GenericRepository<M>, IdentityGenerator, EnvironmentAware {


    private final ModelEntityMapper<M, E> modelEntityMapper;
    private final JpaRepositoryDelegator<E> delegator;
    private final Class<E> targetEntityClass;
    private final SequenceGenerator sequenceGenerator;

    @Autowired
    private EntityManager entityManager;

    public AbstractJpaRepository(JpaRepositoryDelegator<E> delegator, ModelEntityMapper<M, E> modelEntityMapper,
            SequenceGeneratorFactory sequencefactory) {
        this.modelEntityMapper = modelEntityMapper;
        this.delegator = delegator;
        this.targetEntityClass = resolveTargetEntityClass();
        this.sequenceGenerator = sequencefactory.create(targetEntityClass);
    }

    public AbstractJpaRepository(JpaRepositoryDelegator<E> executor, ModelEntityMapper<M, E> modelEntityMapper) {
        this(executor, modelEntityMapper, entityClass -> new DefaultSequenceGenerator(entityClass));
    }

    @Override
    public void setEnvironment(Environment env) {
        sequenceGenerator.configure(env);
    }

    @Override
    public Optional<M> find(Identity id) {
        Optional<E> entity = delegator.findById(id.id());
        return entity.map(modelEntityMapper::toModel);

    }

    @Override
    public List<M> findAll() {
        return delegator.findAllByOrderByIdAsc().stream()
                .map(modelEntityMapper::toModel)
                .toList();
    }

    @Override
    public void add(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        delegator.saveAndFlush(entity);
    }

    @Override
    public void update(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        if (!entityManager.contains(entity)
                && delegator.findById(model.getId().id()).isEmpty()) {
            throw new RmsPersistenceException("target does not exist for id:" + entity.getPk());
        }
        delegator.saveAndFlush(entity);
    }

    @Override
    public void delete(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        if (!entityManager.contains(entity)
                && delegator.findById(model.getId().id()).isEmpty()) {
            throw new RmsPersistenceException("target does not exist for id:" + entity.getPk());
        }
        delegator.delete(entity);
        entityManager.flush();
    }

    @Override
    public int nextIdentity() {
        return (int) sequenceGenerator.generate(entityManager);
    }

    @SuppressWarnings("unchecked")
    protected Class<E> resolveTargetEntityClass() {
        ResolvableType resolvableType = ResolvableType.forClass(AbstractJpaRepository.this.getClass());
        ResolvableType generic = resolvableType.as(AbstractJpaRepository.class).getGeneric(0);
        return (Class<E>) generic.resolve();
    }
}
