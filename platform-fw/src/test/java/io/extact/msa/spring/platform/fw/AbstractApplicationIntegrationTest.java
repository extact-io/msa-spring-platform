package io.extact.msa.spring.platform.fw;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.domain.service.SimpleDuplicateChecker;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.infrastructure.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.TestPersonClient;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.TestPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.TestPersonId;
import io.extact.msa.spring.platform.fw.stub.client.person.infrastructure.TestPersonClientAdapter;
import io.extact.msa.spring.platform.fw.stub.client.person.infrastructure.TestPersonClientApi;
import io.extact.msa.spring.platform.fw.stub.server.person.application.PersonApplicationService;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonFactory;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.server.person.web.PersonController;
import io.extact.msa.spring.platform.fw.web.RestControllerConfig;
import io.extact.msa.spring.test.spring.EnableAutoConfigurationWithoutSecurity;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

/**
 * スタブのPersonアプリを使ってplatform.fwクラスをテストする。
 * <pre>
 * ・スタブアプリ：Component(PersonClient)
 * ・スタブアプリ：Component(PersonClientAdapter)
 * ・スタブアプリ：HTTPインターフェース(PersonApi)
 *     ↓ HTTP
 * ・スタブアプリ：RestController(PersonController)
 * ・スタブアプリ：Component(PersonService)
 * ・スタブアプリ：Component(PersonJpaRepository) or (PersonFileRepository)
 * ※ JPAかFileかどちらの実装を使うかはこのクラスのサブクラスで決定
 * </pre>
 */
@TestMethodOrder(OrderAnnotation.class)
abstract class AbstractApplicationIntegrationTest {

    @Autowired
    protected TestPersonClient client;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfigurationWithoutSecurity // 認証チェックなし
    @Import({ RestControllerConfig.class })
    static class TestConfig {

        @Bean
        PersonApplicationService personService(PersonRepository repository) {
            return new PersonApplicationService(
                    new PersonFactory(repository),
                    new SimpleDuplicateChecker<Person>(repository),
                    repository);
        }

        @Bean
        PersonController personController(PersonApplicationService service) {
            return new PersonController(service);
        }

        @Bean
        TestPersonClient personClient(Environment env) {

            RestClient restClient = RestClient.builder()
                    .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                    .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer()))
                    .build();

            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
            TestPersonClientApi personApi = factory.createClient(TestPersonClientApi.class);

            return new TestPersonClientAdapter(personApi);
        }
    }

    @Test
    @Order(1)
    void testGet() {
        TestPerson expected = TestPerson.reconstruct(1, "name1");
        Optional<TestPerson> actual = client.get(new TestPersonId(1));
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expected);

        actual = client.get(new TestPersonId(999));
        assertThat(actual).isNotPresent();
    }

    @Test
    @Order(2)
    void testGetAll() {
        List<TestPerson> actual = client.getAll();
        assertThat(actual).hasSize(4);
    }

    @Test
    @Order(3)
    void testUpdate() {
        TestPerson expected = TestPerson.reconstruct(4, "UP");
        TestPerson actual = client.update(TestPerson.reconstruct(4, "UP"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Order(4)
    void testUpdateOnValidationError() {
        Throwable thrown = catchThrowable(() -> client.update(TestPerson.reconstruct(4, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(RmsValidationException.class);
    }

    @Test
    @Order(5)
    void testUpdateOnDuplicateError() {
        Throwable thrown = catchThrowable(() -> client.update(TestPerson.reconstruct(2, "name3")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPLICATE);
    }

    @Test
    @Order(6)
    void testUpdateOnNotFound() {
        Throwable thrown = catchThrowable(() -> client.update(TestPerson.reconstruct(999, "UP")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    @Order(7)
    void testAdd() {
        TestPerson expected = TestPerson.reconstruct(newDataId(), "ADD");
        TestPerson actual = client.add("ADD");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Order(8)
    void testAddOnValidationError() {
        Throwable thrown = catchThrowable(() -> client.add("123456")); // 5文字より大きい
        assertThat(thrown).isInstanceOf(RmsValidationException.class);
    }

    @Test
    @Order(9)
    void testAddOnDuplicateError() {
        Throwable thrown = catchThrowable(() -> client.add("name3"));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPLICATE);
    }

    @Test
    @Order(10)
    void testDelete() {
        client.delete(new TestPersonId(newDataId()));
        int actual = client.getAll().size();
        assertThat(actual).isEqualTo(4);
    }

    @Test
    @Order(11)
    void testDeleteOnNotFound() {
        Throwable thrown = catchThrowable(() -> client.delete(new TestPersonId(999)));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    protected abstract int newDataId();
}
