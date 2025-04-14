package io.extact.msa.spring.platform.fw.integ;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.extact.msa.spring.platform.core.env.EnvConfig;
import io.extact.msa.spring.platform.core.log.LogConfig;
import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerConfig;
import io.extact.msa.spring.platform.fw.interfaces.webapi.RmsRestController;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person.PersonCreatable;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote.RemotePerson;
import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote.RemotePersonRepositoryConfig;
import io.extact.msa.spring.test.spring.NopTransactionManager;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("remote")
class RemoteApplicationIntegrationTest extends AbstractApplicationIntegrationTest {

    private static final PersonCreatable testCreator = new PersonCreatable() {
    };

    private static final Person person1 = testCreator.newInstance(new PersonId(1), "name1");
    private static final Person person2 = testCreator.newInstance(new PersonId(2), "name2");
    private static final Person person3 = testCreator.newInstance(new PersonId(3), "name3");
    private static final Person person4 = testCreator.newInstance(new PersonId(4), "name4");

    @Configuration(proxyBeanMethods = false)
    @Import({
        AbstractApplicationIntegrationTest.TestConfig.class,
        RemotePersonRepositoryConfig.class })
    static class TestConfig implements WebMvcConfigurer {

        @Bean
        PlatformTransactionManager nopTransactionManager() {
            return new NopTransactionManager();
        }

        @Configuration(proxyBeanMethods = false)
        @Import({ LogConfig.class, EnvConfig.class, RestControllerConfig.class })
        class PersonStubControllerConfiguration {
            @Bean
            RemotePersonStubController remotePersonStubController() {
                return new RemotePersonStubController();
            }
        }
    }

    @Override
    protected int newDataId() {
        return 5;
    }

    // ------------------------------------------------- test stub controller

    @RmsRestController("/remote-persons")
    static class RemotePersonStubController {

        private Map<Integer, RemotePerson> personsMap;

        @PostConstruct
        void init() {
            personsMap = new LinkedHashMap<>();
            personsMap.put(person1.getId().id(), RemotePerson.from(person1));
            personsMap.put(person2.getId().id(), RemotePerson.from(person2));
            personsMap.put(person3.getId().id(), RemotePerson.from(person3));
            personsMap.put(person4.getId().id(), RemotePerson.from(person4));
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

}
