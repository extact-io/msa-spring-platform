package io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.jpa;

import static jakarta.persistence.AccessType.*;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.TableEntity;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Access(FIELD)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class PersonEntity implements TableEntity<Person> {

    @Id
    private Integer id;
    private String name;

    public static PersonEntity from(Person model) {
        return new PersonEntity(model.getId().id(), model.getName());
    }

    @Override
    public Person toModel() {
        return Person.reconstruct(this.id, this.name);
    }
}
