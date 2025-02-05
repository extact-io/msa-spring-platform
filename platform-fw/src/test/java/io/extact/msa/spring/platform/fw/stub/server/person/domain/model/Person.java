package io.extact.msa.spring.platform.fw.stub.server.person.domain.model;

import jakarta.validation.constraints.NotNull;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupport;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
public class Person implements EntityModel, PersonReference {

    private @NotNull PersonId id;
    private @PersonName String name;

    @ToString.Exclude
    private ModelPropertySupport modelSupport;

    Person(PersonId id, String name) {
        this.id = id;
        this.name = name;
    }

    public void editName(String newName) {
        modelSupport.setPropertyWithValidation("name", newName);
    }

    @Override
    public void configureSupport(ModelPropertySupportFactory factory) {
        this.modelSupport = factory.create(Person::new, this);
    }

    public interface PersonCreatable {
        default Person newInstance(PersonId id, String name) {
            return new Person(id, name);
        }
    }
}
