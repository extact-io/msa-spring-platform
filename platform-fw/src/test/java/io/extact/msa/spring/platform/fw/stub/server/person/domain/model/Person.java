package io.extact.msa.spring.platform.fw.stub.server.person.domain.model;

import jakarta.validation.constraints.NotNull;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id")
@Getter
public class Person implements DomainModel {

    private final @NotNull PersonId id;
    private @PersonName String name;

    public static Person reconstruct(int id, String name) {
        return new Person(new PersonId(id), name);
    }

    public void editName(String name) {
        this.name = name;
    }

    public interface PersonCreatable {
        default Person newInstance(PersonId id, String name) {
            return new Person(id, name);
        }
    }
}
