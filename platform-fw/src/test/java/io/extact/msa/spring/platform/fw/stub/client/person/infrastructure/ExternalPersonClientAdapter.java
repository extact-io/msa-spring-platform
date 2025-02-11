package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import java.util.List;
import java.util.Optional;

import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonClient;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPersonId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExternalPersonClientAdapter implements ExternalPersonClient {

    private final ExternalPersonClientApi api;

    @Override
    public List<ExternalPerson> getAll() {
        return api.getAll().stream()
                .map(ExternalPersonResponse::toModel)
                .toList();
    }

    @Override
    public Optional<ExternalPerson> get(ExternalPersonId personId) {
        return Optional.ofNullable(api.get(personId.id()))
                .map(ExternalPersonResponse::toModel);
    }

    @Override
    public ExternalPerson add(ExternalPerson externalPerson) {
        return api
                .add(externalPerson.transform(ExternalPersonAddRequest::from))
                .toModel();
    }

    @Override
    public ExternalPerson update(ExternalPerson externalPerson) {
        return api
                .update(externalPerson.transform(ExternalPersonUpdateRequest::from))
                .toModel();
    }

    @Override
    public void delete(ExternalPersonId extPersonId) {
        api.delete(extPersonId.id());
    }
}
