package io.extact.msa.spring.platform.fw.stub.remote;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.extact.msa.spring.platform.fw.interfaces.webapi.ApiController;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person.PersonCreatable;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote.RemotePerson;

@ApiController("/remote-persons")
public class RemotePersonStubController {

    private static final PersonCreatable testCreator = new PersonCreatable() {
    };

    static final Person person1 = testCreator.newInstance(new PersonId(1), "name1");
    static final Person person2 = testCreator.newInstance(new PersonId(2), "name2");
    static final Person person3 = testCreator.newInstance(new PersonId(3), "name3");
    static final Person person4 = testCreator.newInstance(new PersonId(4), "name4");

    private Map<Integer, RemotePerson> personsMap;

    @PostConstruct
    void init() {
        personsMap = new LinkedHashMap<>();
        personsMap.put(person1.getId().id(), RemotePerson.from(person1));
        personsMap.put(person2.getId().id(), RemotePerson.from(person2));
        personsMap.put(person3.getId().id(), RemotePerson.from(person3));
        personsMap.put(person4.getId().id(), RemotePerson.from(person4));
    }

    @GetMapping("/reset")
    public void reset() {
        init();
    }

    @GetMapping("/{id}")
    public RemotePerson get(@PathVariable Integer id) {
        return getAll().stream()
                .filter(entity -> entity.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @GetMapping
    public Collection<RemotePerson> getAll() {
        return personsMap.values();
    }

    @PostMapping
    public void add(@RequestBody RemotePerson entity) {
        if (personsMap.putIfAbsent(entity.getId(), entity) != null) {
            throw new IllegalArgumentException("already exists. key:" + entity.getId());
        }
    }

    @PutMapping
    public boolean update(@RequestBody RemotePerson entity) {
        return personsMap.computeIfPresent(entity.getId(), (_, _) -> entity) != null;
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return personsMap.remove(id) != null;
    }

    @GetMapping("/next-identity")
    public int nextIdentity() {
        return Collections.max(personsMap.keySet()) + 1;
    }

    @GetMapping("/unique")
    public RemotePerson findByName(@RequestParam String name) {
        return personsMap.values().stream()
                .filter(person -> person.name().equals(name))
                .findAny()
                .orElse(null);
    }
}