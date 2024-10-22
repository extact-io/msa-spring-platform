package io.extact.msa.spring.platform.fw.persistence.jpa;

@FunctionalInterface
public interface SequenceGeneratorFactory {
    SequenceGenerator create(Class<?> entityClass);
}
