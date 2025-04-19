package io.extact.msa.spring.platform.fw.infrastructure.persistence.remote;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person.PersonCreatable;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote.RemotePerson;

class RemotePersonTest {

    private static final PersonCreatable testCreator = new PersonCreatable() {};

    @Test
    void testConstructor() {
        // given
        Integer id = 1;
        String name = "John Doe";

        // when
        RemotePerson RemotePerson = new RemotePerson(id, name);

        // then
        assertThat(RemotePerson).isNotNull();
        assertThat(RemotePerson.id()).isEqualTo(id);
        assertThat(RemotePerson.name()).isEqualTo(name);
    }

    @Test
    void testFromPerson() {
        // given
        Person person = testCreator.newInstance(new PersonId(1), "John Doe");

        // when
        RemotePerson remotePerson = RemotePerson.from(person);

        // then
        assertThat(remotePerson).isNotNull();
        assertThat(remotePerson.id()).isEqualTo(1);
        assertThat(remotePerson.name()).isEqualTo("John Doe");
    }

    @Test
    void testToModel() {
        // given
        RemotePerson RemotePerson = new RemotePerson(1, "John Doe");

        // when
        Person person = RemotePerson.toModel(null);

        // then
        assertThat(person).isNotNull();
        assertThat(person.getId().id()).isEqualTo(1);
        assertThat(person.getName()).isEqualTo("John Doe");
    }
}
