package io.extact.msa.spring.platform.fw.persistence.file;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;

public interface EntityArrayMapper<T> {

    T toEntity(String[] attributes) throws RmsSystemException;

    String[] toArray(T entity);
}
