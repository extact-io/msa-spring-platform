package io.extact.msa.spring.platform.fw.stub.application.server.web;

import io.extact.msa.spring.platform.fw.domain.Transformable;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonName;

record AddPersonRequest(
        @PersonName //
        String name) implements Transformable {
}
