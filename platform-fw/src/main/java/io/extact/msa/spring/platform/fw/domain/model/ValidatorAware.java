package io.extact.msa.spring.platform.fw.domain.model;

public interface ValidatorAware {
    void configureValidator(DomainValidator validator);
}
