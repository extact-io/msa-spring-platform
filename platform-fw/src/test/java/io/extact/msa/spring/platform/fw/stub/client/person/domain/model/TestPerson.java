package io.extact.msa.spring.platform.fw.stub.client.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id")
public class TestPerson implements DomainModel {

    private @NonNull TestPersonId id;
    private @NonNull @NonFinal @PersonName String name; // TODO: これexperimentalだった

    public static TestPerson reconstruct(int id, String name) {
        return new TestPerson(new TestPersonId(id), name);
    }

    public void changeName(String name) {
        this.name = name;
    }

    public interface PersonCreatable {
        default TestPerson newInstance(TestPersonId id, String name) {
            return new TestPerson(id, name);
        }
    }
}
