package io.extact.msa.spring.platform.fw.stub.client.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.AbstractEntityModel;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class ExternalPerson extends AbstractEntityModel {

    private @NonNull ExternalPersonId id;
    private @NonNull @PersonName String name;

    ExternalPerson(ExternalPersonId id, String name) {
            this.id = id;
            this.name = name;
    }

    public void editName(String newName) {
        applyName(newName);
    }

    private void applyName(String newName) {
        ExternalPerson test = new ExternalPerson();
        test.name = newName;
        validator().validateField(test, test::getName);
        this.name = newName;
    }

    public interface ExternalPersonCreatable {
        default ExternalPerson newInstance(ExternalPersonId id, String name) {
            return new ExternalPerson(id, name);
        }
    }
}
