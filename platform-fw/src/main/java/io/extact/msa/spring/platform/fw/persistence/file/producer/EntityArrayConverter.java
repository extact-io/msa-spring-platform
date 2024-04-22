package io.extact.msa.spring.platform.fw.persistence.file.producer;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;

public interface EntityArrayConverter<T> {

    T toEntity(String[] attributes) throws RmsSystemException;

    String[] toArray(T entity);
}
