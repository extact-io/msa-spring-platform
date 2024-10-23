package io.extact.msa.spring.platform.fw.stub.client.person.domain;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.TestPerson;

@Validated
public interface TestPersonClient {

    List<TestPerson> getAll();

    Optional<TestPerson> get(int id);

    @NotNull
    @Valid
    TestPerson add(String name);

    @Valid
    TestPerson update(TestPerson testPerson);

    void delete(int id);
}
