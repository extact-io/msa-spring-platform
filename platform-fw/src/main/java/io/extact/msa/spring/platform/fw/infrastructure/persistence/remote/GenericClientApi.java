package io.extact.msa.spring.platform.fw.infrastructure.persistence.remote;

import java.util.List;

public interface GenericClientApi<T> {

    T get(Integer id);

    List<T> getAll();

    void add(T entity);

    boolean update(T entity);

    boolean delete(Integer id);

    int nextIdentity();
}
