package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.LoadPathDeriver;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;

@Configuration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class PersonFileRepositoryConfig {

    @Bean
    ModelArrayMapper<Person> personalArrayMapper(ModelValidator validator) {
        return new PersonArrayMapper(validator);
    }

    @Bean
    PersonRepository personFileRepository(Environment env, ModelArrayMapper<Person> mapper) {
        LoadPathDeriver pathDeriver = new LoadPathDeriver(env);
        FileOperator fileOperator = new FileOperator(pathDeriver.derive(PersonFileRepository.FILE_ENTITY));
        return new PersonFileRepository(fileOperator, mapper);
    }
}
