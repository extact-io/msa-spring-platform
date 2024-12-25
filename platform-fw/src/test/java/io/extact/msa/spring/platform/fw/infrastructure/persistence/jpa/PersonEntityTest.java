package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.jpa.PersonEntity;

class PersonEntityTest {

    @Test
    void testConstructor() {
        // given
        Integer id = 1;
        String name = "John Doe";

        // when
        PersonEntity personEntity = new PersonEntity(id, name);

        // then
        assertThat(personEntity).isNotNull();
        assertThat(personEntity.getId()).isEqualTo(id);
        assertThat(personEntity.getName()).isEqualTo(name);
    }

    @Test
    void testFromPerson() {
        // given
        Person person = Person.reconstruct(1, "John Doe");

        // when
        PersonEntity personEntity = PersonEntity.from(person);

        // then
        assertThat(personEntity).isNotNull();
        assertThat(personEntity.getId()).isEqualTo(1);
        assertThat(personEntity.getName()).isEqualTo("John Doe");
    }

    @Test
    void testToModel() {
        // given
        PersonEntity personEntity = new PersonEntity(1, "John Doe");

        // when
        Person person = personEntity.toModel();

        // then
        assertThat(person).isNotNull();
        assertThat(person.getId().id()).isEqualTo(1);
        assertThat(person.getName()).isEqualTo("John Doe");
    }
}
