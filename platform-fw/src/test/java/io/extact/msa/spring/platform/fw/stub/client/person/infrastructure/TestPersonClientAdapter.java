package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import java.util.List;
import java.util.Optional;

import io.extact.msa.spring.platform.fw.stub.client.person.domain.TestPersonClient;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.TestPerson;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestPersonClientAdapter implements TestPersonClient {

    private final TestPersonClientApi api;

    @Override
    public List<TestPerson> getAll() {
        return api.getAll().stream()
                .map(TestPersonResponse::toModel)
                .toList();
    }

    @Override
    public Optional<TestPerson> get(int itemId) {
        return Optional.ofNullable(api.get(itemId))
                .map(TestPersonResponse::toModel);
    }

    @Override
    public TestPerson add(String name) {
        return api.add(name).toModel();
    }

    @Override
    public TestPerson update(TestPerson testPerson) {
        return api.update(testPerson.transform(UpdateTestPersonRequest::from)).toModel();
    }

    @Override
    public void delete(int itemId) {
        api.delete(itemId);
    }
}
