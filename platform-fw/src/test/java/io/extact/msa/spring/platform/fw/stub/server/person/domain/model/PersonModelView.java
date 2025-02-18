package io.extact.msa.spring.platform.fw.stub.server.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.EntityModelView;

public interface PersonModelView extends EntityModelView {

    PersonId getId();
    String getName();
}
