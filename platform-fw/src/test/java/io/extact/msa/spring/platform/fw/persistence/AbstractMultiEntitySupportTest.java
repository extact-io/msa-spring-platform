package io.extact.msa.spring.platform.fw.persistence;

import static io.extact.msa.spring.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.file.EmployeeFileRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.file.PersonFileRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa.EmployeeJpaRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa.PersonJpaRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.model.EmployeeId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.EmployeeRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;

/**
 * 複数エンティティを扱った永続化テスト。
 * FileとJPA実装の機能的なテストはPersonで行っているが、1アプリで複数エンティティを扱えることを
 * 確認するため、Employeeの実装とテストを設けている。
 * EmployeeはFILEでPersonはJPAでテストする
 */
@Transactional
@Rollback
abstract class AbstractMultiEntitySupportTest {

    @Test
    void testMultiGet() {

        Employee employeeExpected = Employee.reconstruct(1, "name1", "dept1");
        Optional<Employee> employeeActual = employeeRepository().find(new EmployeeId(1));

        assertThat(employeeActual).isPresent();
        assertThatToString(employeeActual.get()).isEqualTo(employeeExpected);

        employeeActual = employeeRepository().find(new EmployeeId(99));
        assertThat(employeeActual).isNotPresent();


        Person personExpected = Person.reconstruct(1, "name1");
        Optional<Person> personActual = personRepository().find(new PersonId(1));

        assertThat(personActual).isPresent();
        assertThatToString(personActual.get()).isEqualTo(personExpected);

        personActual = personRepository().find(new PersonId(99));
        assertThat(personActual).isNotPresent();
    }

    protected abstract GenericRepository<Employee> employeeRepository();
    protected abstract PersonRepository personRepository();

    @Nested
    @TestPropertySource(properties = """
            # -- file config
            rms.persistence.employee.api-type=file
            rms.persistence.employee.csv.type=temporary
            rms.persistence.employee.csv.temporary.resource=temporary/employeeTemp.csv
            # -- jpa config
            spring.jpa.hibernate.ddl-auto=none
            spring.sql.init.schema-locations=classpath:sql/person-schema.sql
            spring.sql.init.data-locations=classpath:sql/person-data.sql
            """)
    @DataJpaTest
    static class EmployeeFileAndPersonJpaTest extends AbstractMultiEntitySupportTest {

        @Autowired
        private EmployeeRepository employeeRepository;
        @Autowired
        private PersonRepository personRepository;

        @Configuration(proxyBeanMethods = false)
        @Import({ EmployeeFileRepositoryConfig.class, PersonJpaRepositoryConfig.class })
        static class TestConfig {
        }

        @Override
        protected EmployeeRepository employeeRepository() {
            return employeeRepository;
        }

        @Override
        protected PersonRepository personRepository() {
            return personRepository;
        }
    }

    @Nested
    @TestPropertySource(properties = """
            # -- jpa config
            spring.jpa.hibernate.ddl-auto=none
            spring.sql.init.schema-locations=classpath:sql/employee-schema.sql
            spring.sql.init.data-locations=classpath:sql/employee-data.sql
            # -- file config
            rms.persistence.person.api-type=file
            rms.persistence.person.csv.type=temporary
            rms.persistence.person.csv.temporary.resource=temporary/personTemp.csv
            """)
    @DataJpaTest
    static class EmployeeJpaAndPersonFileTest extends AbstractMultiEntitySupportTest {

        @Autowired
        private GenericRepository<Employee> employeeRepository;
        @Autowired
        private PersonRepository personRepository;

        @Configuration(proxyBeanMethods = false)
        @Import({ EmployeeJpaRepositoryConfig.class, PersonFileRepositoryConfig.class })
        static class TestConfig {
        }

        @Override
        protected GenericRepository<Employee> employeeRepository() {
            return employeeRepository;
        }

        @Override
        protected PersonRepository personRepository() {
            return personRepository;
        }
    }
}
