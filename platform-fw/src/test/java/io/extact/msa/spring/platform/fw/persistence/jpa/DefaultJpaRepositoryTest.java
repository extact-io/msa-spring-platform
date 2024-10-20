package io.extact.msa.spring.platform.fw.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa.EmployeeEntity;
import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa.EmployeeJpaRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;

@DataJpaTest
@ActiveProfiles("jpa")
class DefaultJpaRepositoryTest {

    @Autowired
    private DefaultJpaRepository<Employee, EmployeeEntity> repository;

    @Configuration(proxyBeanMethods = false)
    @Import(EmployeeJpaRepositoryConfig.class)
    static class TestConfig {
    }

    @Test
    void test() {
        fail();
    }
}
