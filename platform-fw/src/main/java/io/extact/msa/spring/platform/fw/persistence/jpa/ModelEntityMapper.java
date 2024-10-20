package io.extact.msa.spring.platform.fw.persistence.jpa;

public interface ModelEntityMapper<M, E> {

    M toModel(E entity);

    E toEntity(M model);
}
