package io.extact.msa.spring.platform.fw.stub.server.person.web;

import io.extact.msa.spring.platform.fw.domain.model.Transformable;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;

record AddPersonRequest(
        @PersonName //
        String name) implements Transformable {
}
