package io.extact.msa.spring.platform.fw.persistence.jpa;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import io.extact.msa.spring.platform.fw.domain.Identifiable;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;

public abstract class AbstractJpaRepository<T extends Identifiable> implements GenericRepository<T> {

    @Override
    public Optional<T> get(int id) {
        return innerRepository().findById(id);
    }

    @Override
    public List<T> findAll() {
        return innerRepository().findAllByOrderByIdAsc();
    }

    @Override
    public void add(T entity) {
        innerRepository().saveAndFlush(entity);
    }

    @Override
    public Optional<T> update(T entity) {
        if (!entityManager().contains(entity) && get(entity.getId()).isEmpty()) {
            return Optional.empty();
        }
        T updated = innerRepository().saveAndFlush(entity);
        return Optional.of(updated);
    }

    @Override
    public void delete(T entity) {
        innerRepository().delete(entity);
        entityManager().flush();
    }

    public EntityManager entityManager() {
        return innerRepository().entityManager();
    }

    protected abstract SpringDataJpaInnerRepository<T> innerRepository();
}
