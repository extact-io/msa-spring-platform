package io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.file;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.LoadPathDeriver;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;

@TestConfiguration(proxyBeanMethods = false)
@Import(ValidationConfig.class)
public class PersonFileRepositoryConfig {

    @Bean
    ModelArrayMapper<Person> personalArrayMapper() {
        return PersonalArrayMapper.INSTANCE;
    }

    @Bean
    PersonRepository personFileRepository(Environment env, ModelArrayMapper<Person> mapper) {
        LoadPathDeriver pathDeriver = new LoadPathDeriver(env);
        FileOperator fileOperator = new FileOperator(pathDeriver.derive(PersonFileRepository.FILE_ENTITY));
        return new PersonFileRepository(fileOperator, mapper);
    }
}
