package io.extact.msa.spring.platform.fw.stub.application.client.external;

import java.util.List;
import java.util.Optional;

import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.AddPersonClientRequest;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.PersonClientResponse;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.UpdatePersonClientRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonClientAdapter implements PersonClient {

    private final PersonApi api;

    @Override
    public List<PersonClientResponse> getAll() {
        return api.getAll();
    }

    @Override
    public Optional<PersonClientResponse> get(int itemId) {
        return Optional.ofNullable(api.get(itemId));
    }

    @Override
    public PersonClientResponse add(AddPersonClientRequest req) {
        return api.add(req);
    }

    @Override
    public PersonClientResponse update(UpdatePersonClientRequest req) {
        return api.update(req);
    }

    @Override
    public void delete(int itemId) {
        api.delete(itemId);
    }
}
