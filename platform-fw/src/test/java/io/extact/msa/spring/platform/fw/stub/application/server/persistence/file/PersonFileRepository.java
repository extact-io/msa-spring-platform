package io.extact.msa.spring.platform.fw.stub.application.server.persistence.file;

import io.extact.msa.spring.platform.fw.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.spring.platform.fw.persistence.file.producer.EntityArrayConverter;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
//@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class PersonFileRepository extends AbstractFileRepository<Person> {

    @Inject
    public PersonFileRepository(FileAccessor fileAccessor, EntityArrayConverter<Person> converter) {
        super(fileAccessor, converter);
    }
}
