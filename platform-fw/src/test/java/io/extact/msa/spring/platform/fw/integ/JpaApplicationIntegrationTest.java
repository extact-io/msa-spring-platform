package io.extact.msa.spring.platform.fw.integ;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.method.MethodValidationException;

import io.extact.msa.spring.platform.fw.infrastructure.framework.sqlinit.ProfileBasedDbInitializerConfig;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonCreator;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPersonId;
import io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.jpa.PersonJpaRepositoryConfig;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("person-jpa")
class JpaApplicationIntegrationTest extends AbstractApplicationIntegrationTest {

    @Configuration(proxyBeanMethods = false)
    @Import({
        AbstractApplicationIntegrationTest.TestConfig.class,
        ProfileBasedDbInitializerConfig.class,
        PersonJpaRepositoryConfig.class })
    static class TestConfig {
    }

    @Override
    protected int newDataId() {
        return 1000;
    }

    @Test
    @Order(99)
    void testValidateResponseErrorWithAdd(@Autowired ExternalPersonCreator creator) {

        // given
        String name = "error"; // サーバー側で桁数オーバーの文字列を返すキーワード
        ExternalPerson addPerson = creator.create(name);

        // when &then
        assertThrows(MethodValidationException.class, () -> {
            // クライアントサイドの戻り値に対するバリデートでエラー
            client.add(addPerson);
        });
    }

    @Test
    @Order(99)
    void testValidateResponseErrorWithUpdate() {

        // given
        ExternalPersonId id = new ExternalPersonId(4);
        String name = "error"; // サーバー側で桁数オーバーの文字列を返すキーワード
        ExternalPerson updatePerson = testCreator.newInstance(id, name);

        // when
        assertThrows(MethodValidationException.class, () -> {
            // クライアントサイドの戻り値に対するバリデートでエラー
            client.update(updatePerson);
        });
    }
}
