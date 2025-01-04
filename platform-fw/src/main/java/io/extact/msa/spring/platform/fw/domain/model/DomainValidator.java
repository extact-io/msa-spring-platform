package io.extact.msa.spring.platform.fw.domain.model;

public interface DomainValidator {

    void validateModel(DomainModel model, Object... groups);

    void validateProperty(String propName, Object value, Object... groups);
}
