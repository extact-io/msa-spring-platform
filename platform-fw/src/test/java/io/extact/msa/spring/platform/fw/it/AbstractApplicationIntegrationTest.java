package io.extact.msa.spring.platform.fw.it;

import static io.extact.msa.spring.test.assertj.ToStringAssert.*;
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

import io.extact.msa.spring.platform.fw.controller.RestControllerConfig;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.stub.application.client.external.PersonApi;
import io.extact.msa.spring.platform.fw.stub.application.client.external.PersonClient;
import io.extact.msa.spring.platform.fw.stub.application.client.external.PersonClientAdapter;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.AddPersonClientRequest;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.PersonClientResponse;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.UpdatePersonClientRequest;
import io.extact.msa.spring.platform.fw.stub.application.server.application.PersonApplicationService;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.web.PersonController;
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
    protected PersonClient client;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfigurationWithoutSecurity
    @Import({ RestControllerConfig.class })
    static class TestConfig {

        @Bean
        PersonApplicationService personService(PersonRepository repository) {
            return new PersonApplicationService(repository);
        }

        @Bean
        PersonController personController(PersonApplicationService service) {
            return new PersonController(service);
        }

        @Bean
        PersonClient personClient(Environment env) {

            RestClient restClient = RestClient.builder()
                    .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                    .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer()))
                    .build();

            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
            PersonApi personApi = factory.createClient(PersonApi.class);

            return new PersonClientAdapter(personApi);
        }
    }

    @Test
    @Order(1)
    void testGet() {
        PersonClientResponse expected = new PersonClientResponse(1, "name1");
        Optional<PersonClientResponse> actual = client.get(1);
        assertThat(actual).isPresent();
        assertThatToString(actual.get()).isEqualTo(expected);

        actual = client.get(999);
        assertThat(actual).isNotPresent();
    }

    @Test
    @Order(2)
    void testGetAll() {
        List<PersonClientResponse> actual = client.getAll();
        assertThat(actual).hasSize(4);
    }

    @Test
    @Order(3)
    void testUpdate() {
        PersonClientResponse expected = new PersonClientResponse(4, "UP");
        PersonClientResponse actual = client.update(new UpdatePersonClientRequest(4, "UP"));
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @Order(4)
    void testUpdateOnValidationError() {
        Throwable thrown = catchThrowable(() -> client.update(new UpdatePersonClientRequest(4, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(RmsValidationException.class);
    }

    @Test
    @Order(5)
    void testUpdateOnDuplicateError() {
        Throwable thrown = catchThrowable(() -> client.update(new UpdatePersonClientRequest(2, "name3")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPLICATE);
    }

    @Test
    @Order(6)
    void testUpdateOnNotFound() {
        Throwable thrown = catchThrowable(() -> client.update(new UpdatePersonClientRequest(999, "UP")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    @Order(7)
    void testAdd() {
        PersonClientResponse expected = new PersonClientResponse(newDataId(), "ADD");
        PersonClientResponse actual = client.add(new AddPersonClientRequest("ADD"));
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @Order(8)
    void testAddOnValidationError() {
        Throwable thrown = catchThrowable(() -> client.add(new AddPersonClientRequest("123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(RmsValidationException.class);
    }

    @Test
    @Order(9)
    void testAddOnDuplicateError() {
        Throwable thrown = catchThrowable(() -> client.add(new AddPersonClientRequest("name3")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPLICATE);
    }

    @Test
    @Order(10)
    void testDelete() {
        client.delete(newDataId());
        int actual = client.getAll().size();
        assertThat(actual).isEqualTo(4);
    }

    @Test
    @Order(11)
    void testDeleteOnNotFound() {
        Throwable thrown = catchThrowable(() -> client.delete(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    protected abstract int newDataId();
}
