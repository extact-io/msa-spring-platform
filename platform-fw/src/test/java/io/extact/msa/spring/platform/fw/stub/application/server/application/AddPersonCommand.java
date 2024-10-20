package io.extact.msa.spring.platform.fw.stub.application.server.application;

import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonName;

record AddPersonCommand(
        @PersonName //
        String name) {
}
