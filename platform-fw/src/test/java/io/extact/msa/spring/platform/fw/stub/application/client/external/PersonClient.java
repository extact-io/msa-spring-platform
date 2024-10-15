package io.extact.msa.spring.platform.fw.stub.application.client.external;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.AddPersonClientRequest;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.PersonClientResponse;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.UpdatePersonClientRequest;

@Validated
public interface PersonClient {

    List<PersonClientResponse> getAll();

    Optional<PersonClientResponse> get(int id);

    @NotNull
    @Valid
    PersonClientResponse add(AddPersonClientRequest req);

    @Valid
    PersonClientResponse update(UpdatePersonClientRequest req);

    void delete(int id);
}
