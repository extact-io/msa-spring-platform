package io.extact.msa.spring.platform.fw.stub.client.person.domain;

import io.extact.msa.spring.platform.fw.domain.model.ModelCreator;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson.ExternalPersonCreatable;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPersonId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExternalPersonCreator implements ModelCreator<ExternalPerson, String> {

    private final ModelValidator validator;
    private final ModelPropertySupportFactory modelSupportFactory;
    private final ExternalPersonCreatable constructorProxy = new ExternalPersonCreatable() {};

    public ExternalPerson create(String name) {

        ExternalPerson extPerson = constructorProxy.newInstance(ExternalPersonId.TRANSIENT_ID, name);

        extPerson.configureSupport(modelSupportFactory);
        validator.validateModel(extPerson);

        return extPerson;
    }
}
