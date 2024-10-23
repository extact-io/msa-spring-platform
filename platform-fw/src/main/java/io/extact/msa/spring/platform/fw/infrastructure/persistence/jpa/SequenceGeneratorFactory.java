package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

@FunctionalInterface
public interface SequenceGeneratorFactory {
    SequenceGenerator create(Class<?> entityClass);
}
