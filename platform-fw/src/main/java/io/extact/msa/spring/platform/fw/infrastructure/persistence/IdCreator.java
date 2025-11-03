package io.extact.msa.spring.platform.fw.infrastructure.persistence;

import io.extact.msa.spring.platform.fw.domain.model.Identity;

@FunctionalInterface
public interface IdCreator<I extends Identity> {
    I create(int id);
}
