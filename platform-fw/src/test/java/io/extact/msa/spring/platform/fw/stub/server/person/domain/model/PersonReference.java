package io.extact.msa.spring.platform.fw.stub.server.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.EntityModelReference;

public interface PersonReference extends EntityModelReference {

    PersonId getId();
    String getName();
}
