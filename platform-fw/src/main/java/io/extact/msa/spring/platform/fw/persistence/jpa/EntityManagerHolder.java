package io.extact.msa.spring.platform.fw.persistence.jpa;

import jakarta.persistence.EntityManager;

public interface EntityManagerHolder<E, ID> {

    EntityManager entityManager();

    Class<?> entityClass();
}
