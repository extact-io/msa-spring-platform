package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.remote.AbstractRemoteRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;

public class RemotePersonRepository extends AbstractRemoteRepository<Person, RemotePerson> implements PersonRepository {

    private final RemotePersonClientApi clientApi;
    private final ModelEntityMapper<Person, RemotePerson> entityMapper;

    public RemotePersonRepository(
            RemotePersonClientApi clientApi,
            ModelEntityMapper<Person, RemotePerson> entityMapper) {

        super(clientApi, entityMapper);
        this.clientApi = clientApi;
        this.entityMapper = entityMapper;
    }

    public Optional<Person> findDuplicationData(Person checkModel) {
        RemotePerson found = clientApi.findByName(checkModel.getName());
        return Optional
                .ofNullable(found)
                .map(entityMapper::toModel);
    }
}
