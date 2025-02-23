package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

public interface ModelEntityMapper<M, E> {

    M toModel(E entity);

    E toEnity(M model);
}
