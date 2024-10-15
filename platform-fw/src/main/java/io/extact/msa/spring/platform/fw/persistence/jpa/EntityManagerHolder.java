package io.extact.msa.spring.platform.fw.persistence.jpa;

import jakarta.persistence.EntityManager;

public interface EntityManagerHolder {
    EntityManager entityManager();
}
