package io.extact.msa.spring.platform.fw.integ;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.feature.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.stub.apps.person.application.PersonServiceConfig;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonDomainConfig;
import io.extact.msa.spring.platform.fw.stub.apps.person.web.PersonControllerConfig;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonClient;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonCreator;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonDomainConfig;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson.ExternalPersonCreatable;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPersonId;
import io.extact.msa.spring.platform.fw.stub.client.person.infrastructure.ExternalPersonClientConfig;
import io.extact.msa.spring.test.assertj.ToStringAssert;
import io.extact.msa.spring.test.spring.EnableAutoConfigurationWithoutSecurityAndActuator;

/**
 * スタブのPersonアプリを使ってplatform.fwクラスをテストする。
 * client.person(ExternalPersonClient) → apps.person
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

    protected static final ExternalPersonCreatable testCreator = new ExternalPersonCreatable() {};

    @Autowired
    protected ExternalPersonClient client;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfigurationWithoutSecurityAndActuator // 認証とactuatorを除外
    @Import({
            PersonDomainConfig.class,
            PersonServiceConfig.class,
            PersonControllerConfig.class,
            ExternalPersonDomainConfig.class,
            ExternalPersonClientConfig.class})
    static class TestConfig {
    }

    @Test
    @Order(1)
    void testGet() {

        // given -- match
        ExternalPersonId id = new ExternalPersonId(1);
        // when
        Optional<ExternalPerson> actual = client.get(id);
        // then
        ExternalPerson expected = testCreator.newInstance(new ExternalPersonId(1), "name1");
        assertThat(actual).isPresent();
        ToStringAssert.assertThatToString(actual.get()).isEqualTo(expected);

        // given -- unmatch
        id = new ExternalPersonId(999);
        // when
        actual = client.get(id);
        // then
        assertThat(actual).isNotPresent();
    }

    @Test
    @Order(2)
    void testGetAll() {
        // given
        // when
        List<ExternalPerson> actual = client.getAll();
        // then
        assertThat(actual).hasSize(4);
    }

    @Test
    @Order(3)
    void testUpdate() {
        // given
        ExternalPersonId id = new ExternalPersonId(4);
        String name = "up";
        ExternalPerson updatePerson = testCreator.newInstance(id, name);

        // when
        ExternalPerson actual = client.update(updatePerson);

        // then
        ToStringAssert.assertThatToString(actual).isEqualTo(updatePerson);
    }

    @Test
    @Order(4)
    void testUpdateOnValidationError() {

        // given
        ExternalPersonId id = new ExternalPersonId(4);
        String name = "123456"; // 5文字より大きい
        ExternalPerson updatePerson = testCreator.newInstance(id, name);

        // when
        RmsValidationException thrown = assertThrows(RmsValidationException.class, () -> {
            client.update(updatePerson);
        });

        // then
        assertThat(thrown.getErrorMessage().messageItems()).hasSize(1);
        assertThat(thrown.getDetailMessage()).contains("名前");
    }

    @Test
    @Order(5)
    void testUpdateOnDuplicateError() {

        // given
        ExternalPersonId id = new ExternalPersonId(2);
        String name = "name3"; // 既にあるname
        ExternalPerson updatePerson = testCreator.newInstance(id, name);

        // when
        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            client.update(updatePerson);
        });

        // then
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.DUPLICATE);
    }

    @Test
    @Order(6)
    void testUpdateOnNotFound() {

        // given
        ExternalPersonId id = new ExternalPersonId(999); // not exist
        String name = "UP";
        ExternalPerson updatePerson = testCreator.newInstance(id, name);

        // when
        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            client.update(updatePerson);
        });

        // then
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    @Order(7)
    void testAdd(@Autowired ExternalPersonCreator creator) {
        // given
        String name = "ADD";
        ExternalPerson addPerson = creator.create(name);

        // when
        ExternalPerson actual = client.add(addPerson);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(new ExternalPersonId(newDataId()));
        assertThat(actual.getName()).isEqualTo(addPerson.getName());
    }

    @Test
    @Order(8)
    void testAddOnValidationError(@Autowired ExternalPersonCreator creator) {
        // given
        String name = "123456"; // 5文字より大きい

        // when
        RmsValidationException thrown = assertThrows(RmsValidationException.class, () -> {
            creator.create(name);
        });

        // then
        assertThat(thrown.getErrorMessage().messageItems()).hasSize(1);
        assertThat(thrown.getDetailMessage()).contains("名前");
    }

    @Test
    @Order(9)
    void testAddOnDuplicateError(@Autowired ExternalPersonCreator creator) {
        // given
        String name = "name3"; // 既にあるname
        ExternalPerson addPerson = creator.create(name);

        // when
        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            client.add(addPerson);
        });

        // then
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.DUPLICATE);
    }

    @Test
    @Order(10)
    void testDelete() {
        // given
        ExternalPersonId id = new ExternalPersonId(newDataId());

        // when
        client.delete(id);

        // then
        int actual = client.getAll().size();
        assertThat(actual).isEqualTo(4);
    }

    @Test
    @Order(11)
    void testDeleteOnNotFound() {
        // given
        ExternalPersonId id = new ExternalPersonId(999); // not exist

        // when
        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            client.delete(id);
        });

        // then
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    protected abstract int newDataId();
}
