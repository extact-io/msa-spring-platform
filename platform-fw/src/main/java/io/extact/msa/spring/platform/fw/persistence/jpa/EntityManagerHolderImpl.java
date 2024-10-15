package io.extact.msa.spring.platform.fw.persistence.jpa;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityManagerHolderImpl implements EntityManagerHolder {

    private final EntityManager entityManager;

    @Override
    public EntityManager entityManager() {
        return entityManager;
    }
}
