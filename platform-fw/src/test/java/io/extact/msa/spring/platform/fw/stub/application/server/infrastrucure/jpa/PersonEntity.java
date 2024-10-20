package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import static jakarta.persistence.AccessType.*;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Delete;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.spring.platform.fw.persistence.jpa.TableEntity;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Access(FIELD)
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "valueOf")
@Getter @Setter
@ToString
public class PersonEntity implements TableEntity<Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = { Update.class, Delete.class })
    private Integer id;
    @PersonName
    private String name;

    public static PersonEntity ofTransient(String name) {
        return PersonEntity.valueOf(null, name);
    }

    @Override
    public Person toModel() {
        return Person.reconstruct(new PersonId(this.id), this.name);
    }
}
