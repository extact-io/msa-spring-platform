package io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa;

import io.extact.msa.spring.platform.fw.persistence.jpa.JpaCrudRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
//@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.JPA)
public class PersonJpaRepository extends JpaCrudRepository<Person> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager getEntityManage() {
        return this.em;
    }

    @Override
    public Class<Person> getTargetClass() {
        return Person.class;
    }

}
