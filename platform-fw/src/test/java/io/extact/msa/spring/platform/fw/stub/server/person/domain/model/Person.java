package io.extact.msa.spring.platform.fw.stub.server.person.domain.model;

import jakarta.validation.constraints.NotNull;

import io.extact.msa.spring.platform.fw.domain.model.AbstractEntityModel;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class Person extends AbstractEntityModel implements PersonModelView {

    private @NotNull PersonId id;
    private @PersonName String name;

    Person(PersonId id, String name) {
        this.id = id;
        this.name = name;
    }

    public void editName(String newName) {
        applyName(newName);
    }

    public void editNameWithoutValidation(String newName) {
        this.name = newName;
    }

    private void applyName(String newName) {
        Person test = new Person();
        test.name = newName;
        validator().validateField(test, test::getName);
        this.name = newName;
    }

    public interface PersonCreatable {
        default Person newInstance(PersonId id, String name) {
            return new Person(id, name);
        }
    }
}
