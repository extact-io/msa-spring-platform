package io.extact.msa.spring.platform.fw.stub.client.person.domain;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPersonId;

@Validated
public interface ExternalPersonClient {

    List<ExternalPerson> getAll();

    Optional<ExternalPerson> get(ExternalPersonId id);

    @NotNull
    @Valid
    ExternalPerson add(ExternalPerson externalPerson);

    @Valid
    ExternalPerson update(ExternalPerson externalPerson);

    void delete(ExternalPersonId id);
}
