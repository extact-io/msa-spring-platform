package io.extact.msa.spring.platform.fw.persistence.jpa;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import io.extact.msa.spring.platform.fw.domain.Identity;
import io.extact.msa.spring.platform.fw.exception.RmsPersistenceException;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;

public abstract class AbstractJpaRepository<M extends DomainModel, E extends TableEntity<M>>
        implements GenericRepository<M> {


    private final ModelEntityMapper<M, E> modelEntityMapper;
    private final SpringDataJpaExecutor<E> executor;
    private final Class<E> targetEntityClass;

    @Autowired
    private EntityManager entityManager;
    private SequenceGenerator sequenceGenerator;


    public AbstractJpaRepository(SpringDataJpaExecutor<E> executor, ModelEntityMapper<M, E> modelEntityMapper) {
        this.modelEntityMapper = modelEntityMapper;
        this.executor = executor;
        this.targetEntityClass = resolveTargetEntityClass();
        this.sequenceGenerator = new SequenceGenerator(targetEntityClass);
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
        E entity = model.transform(modelEntityMapper::toEntity);
        executor.saveAndFlush(entity);
    }

    @Override
    public void update(M model) {
        E entity = model.transform(modelEntityMapper::toEntity);
        if (!entityManager.contains(entity)
                && executor.findById(model.getId().id()).isEmpty()) {
            new RmsPersistenceException("target does not exist for pk:" + entity.getPk());
        }
        executor.saveAndFlush(entity);
    }

    @Override
    public void delete(M model) {
        E entity = model.transform(modelEntityMapper::toEntity);
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

    static class SequenceGenerator {

        final String sequenceName;
        String databaseName;

        SequenceGenerator(Class<?> entityClass) {
            sequenceName = resolveSequenceName(entityClass);
        }

        String resolveSequenceName(Class<?> entityClass) {

            String entityClassName = entityClass.getSimpleName().toLowerCase();

            if (entityClassName.endsWith("entity")) {
                return entityClassName.substring(0, entityClassName.length() - "entity".length()) + "_seq";
            } else {
                return entityClassName.toLowerCase() + "_seq";
            }
        }

        long generate(EntityManager entityManager) {
            preperDatabaseName(entityManager);
            String sql = resolveSql();
            Query query = entityManager.createNativeQuery(sql);
            return (Long) query.getSingleResult();
        }

        void preperDatabaseName(EntityManager entityManager) {
            if (databaseName == null) {
                try {
                    Session session = entityManager.unwrap(Session.class);
                    Connection connection = session.doReturningWork(conn -> conn);
                    databaseName = connection.getMetaData().getDatabaseProductName();
                } catch (SQLException e) {
                    throw new RmsPersistenceException(e);
                }
            }
        }

        private String resolveSql() {
            return switch (databaseName) {
                case "H2" -> {
                    String template = "SELECT NEXT VALUE FOR %s;"; // for H2
                    yield template.formatted(sequenceName);
                }
                default -> {
                    String template = "SELECT NEXT VALUE FOR %s;"; // for H2
                    yield template.formatted(sequenceName);
                }
            };
        }
    }
}
