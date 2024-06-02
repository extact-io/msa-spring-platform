package io.extact.msa.spring.platform.fw.persistence.jpa;

import java.util.List;

import io.extact.msa.spring.platform.fw.domain.IdProperty;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;
import jakarta.persistence.EntityManager;

public abstract class JpaCrudRepository<T extends IdProperty> implements GenericRepository<T> {

    @Override
    public T get(int id) {
        return this.getEntityManage().find(this.getTargetClass(), id);
    }

    @Override
    public List<T> findAll() {
        var jpql = "select e from " + this.getTargetClass().getSimpleName() + " e order by e.id";
        return this.getEntityManage().createQuery(jpql, this.getTargetClass())
                .getResultList();
    }

    @Override
    public void add(T entity) {
        this.getEntityManage().persist(entity);
        this.getEntityManage().flush();
    }

    @Override
    public T update(T entity) {
        if (!this.getEntityManage().contains(entity) && get(entity.getId()) == null) {
            return null;
        }
        var updated = this.getEntityManage().merge(entity);
        this.getEntityManage().flush();
        return updated;
    }

    @Override
    public void delete(T entity) {
        this.getEntityManage().remove(entity);
        this.getEntityManage().flush();
    }

    protected abstract EntityManager getEntityManage();

    protected abstract Class<T> getTargetClass();
}
