package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.exception.RmsPersistenceException;
import io.extact.msa.spring.platform.fw.infrastructure.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.GenericRepository;

public abstract class AbstractJpaRepository<M extends DomainModel, E extends TableEntity<M>>
        implements GenericRepository<M>, EnvironmentAware {


    private final ModelEntityMapper<M, E> modelEntityMapper;
    private final SpringDataJpaExecutor<E> executor;
    private final Class<E> targetEntityClass;
    private final SequenceGenerator sequenceGenerator;

    @Autowired
    private EntityManager entityManager;

    public AbstractJpaRepository(SpringDataJpaExecutor<E> executor, ModelEntityMapper<M, E> modelEntityMapper,
            SequenceGeneratorFactory sequencefactory) {
        this.modelEntityMapper = modelEntityMapper;
        this.executor = executor;
        this.targetEntityClass = resolveTargetEntityClass();
        this.sequenceGenerator = sequencefactory.create(targetEntityClass);
    }

    public AbstractJpaRepository(SpringDataJpaExecutor<E> executor, ModelEntityMapper<M, E> modelEntityMapper) {
        this(executor, modelEntityMapper, entityClass -> new DefaultSequenceGenerator(entityClass));
    }

    @Override
    public void setEnvironment(Environment env) {
        sequenceGenerator.configure(env);
    }

    @Override
    public Optional<M> find(Identity id) {
        Optional<E> entity = executor.findById(id.id());
        return entity.map(E::toModel);

    }

    @Override
    public List<M> findAll() {
        return executor.findAllByOrderByIdAsc().stream()
                .map(E::toModel)
                .toList();
    }

    @Override
    public void add(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        executor.saveAndFlush(entity);
    }

    @Override
    public void update(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        if (!entityManager.contains(entity)
                && executor.findById(model.getId().id()).isEmpty()) {
            new RmsPersistenceException("target does not exist for pk:" + entity.getPk());
        }
        executor.saveAndFlush(entity);
    }

    @Override
    public void delete(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        executor.delete(entity);
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
