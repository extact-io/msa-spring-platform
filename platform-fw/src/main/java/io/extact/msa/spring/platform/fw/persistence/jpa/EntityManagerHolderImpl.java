package io.extact.msa.spring.platform.fw.persistence.jpa;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

public class EntityManagerHolderImpl<E, ID> implements EntityManagerHolder<E, ID> {

    private final EntityManager entityManager;
    private final Class<?> entityClass;


    public EntityManagerHolderImpl(JpaEntityInformation<E, ID> entityInformation, JpaContext jpaContext) {
        this.entityClass = entityInformation.getJavaType();
        this.entityManager = jpaContext.getEntityManagerByManagedType(entityClass);
    }

    @Override
    public EntityManager entityManager() {
        return entityManager;
    }

    @Override
    public Class<?> entityClass() {
        return entityClass;
    }
}
