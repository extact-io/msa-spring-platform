package io.extact.msa.spring.platform.fw.stub.application.server.persistence.file;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.persistence.file.EntityArrayMapper;
import io.extact.msa.spring.platform.fw.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.persistence.file.io.LoadPathDeriver;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.PersonRepository;

@TestConfiguration(proxyBeanMethods = false)
@Import(ValidationConfiguration.class)
public class PersonFileRepositoryConfig {

    @Bean
    FileOperator fileOperator(Environment env) throws IOException {
        LoadPathDeriver pathDeriver = new LoadPathDeriver(env);
        return new FileOperator(pathDeriver.derive(PersonFileRepository.FILE_ENTITY));
    }

    @Bean
    EntityArrayMapper<Person> personalArrayMapper() {
        return PersonalArrayMapper.INSTANCE;
    }

    @Bean
    @Primary
    PersonRepository personFileRepository(FileOperator fileOperator, EntityArrayMapper<Person> mapper) {
        return new PersonFileRepository(fileOperator, mapper);
    }

    @Bean
    @Scope("prototype")
    @Qualifier("prototype") // for unit test
    PersonRepository prototypePersonFileRepository(Environment env) throws IOException {
        FileOperator fileOperator = fileOperator(env); // Bean生成の都度ファイルを再配置する
        return new PersonFileRepository(fileOperator, personalArrayMapper());
    }
}
