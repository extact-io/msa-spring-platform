package io.extact.msa.spring.platform.fw.domain.model;

public interface ModelCreator<M extends EntityModel, T> {
    M create(T attributes);
}
