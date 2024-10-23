package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;

import org.springframework.core.env.Environment;

public interface SequenceGenerator {

    void configure(Environment env);

    long generate(EntityManager entityManager);
}
