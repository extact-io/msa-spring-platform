package io.extact.msa.spring.platform.fw.infrastructure.persistence.file;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.PersonFileRepositoryTest.TestConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.LoadPathDeriver;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.file.PersonFileRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.file.PersonFileRepositoryConfig;
import io.extact.msa.spring.test.spring.NopTransactionManager;
import io.extact.msa.spring.test.spring.SelfRootContext;

@SpringBootTest(classes = { SelfRootContext.class, TestConfig.class }, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("file")
class PersonFileRepositoryTest extends AbstractPersonRepositoryTest {

    private PersonRepository repository;

    @TestConfiguration(proxyBeanMethods = false)
    @Import(PersonFileRepositoryConfig.class)
    static class TestConfig {

        @Bean
        @Primary
        @Scope("prototype")
        PersonRepository prototypePersonFileRepository(Environment env, ModelArrayMapper<Person> mapper)
                throws IOException {
            LoadPathDeriver pathDeriver = new LoadPathDeriver(env); // Bean生成の都度ファイルを再配置する
            FileOperator fileOperator = new FileOperator(pathDeriver.derive(PersonFileRepository.FILE_ENTITY));
            return new PersonFileRepository(fileOperator, mapper);
        }

        @Bean
        PlatformTransactionManager nopTransactionManager() {
            return new NopTransactionManager();
        }
    }

    // prototypeスコープにして毎回ファイルの初期が行われるようにする
    @BeforeEach
    void beforeEach(@Autowired PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    protected PersonRepository repository() {
        return repository;
    }

    @Test
    @Override
    protected void testNextIdentity() {
        // when
        int firstTime = repository.nextIdentity();
        repository.add(testCreator.newInstance(new PersonId(firstTime), "1st"));
        int secondTime = repository.nextIdentity();
        repository.add(testCreator.newInstance(new PersonId(secondTime), "2nd"));
        int thirdTime = repository.nextIdentity();
        repository.add(testCreator.newInstance(new PersonId(thirdTime), "3rd"));
        // then
        assertThat(secondTime).isEqualTo(firstTime + 1);
        assertThat(thirdTime).isEqualTo(secondTime + 1);
    }
}
