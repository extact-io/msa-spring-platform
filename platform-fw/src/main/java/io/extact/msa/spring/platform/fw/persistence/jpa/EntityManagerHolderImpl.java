package io.extact.msa.spring.platform.fw.persistence.jpa;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityManagerHolderImpl<E> implements EntityManagerHolder<E> {

    private final EntityManager entityManager;

    @Override
    public EntityManager entityManager() {
        return entityManager;
    }

    @Override
    public boolean isManaged(E entity) {
        return entityManager.contains(entity);
    }
}
