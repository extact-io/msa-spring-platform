package io.extact.msa.spring.platform.fw.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.domain.Identifiable;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;

@Transactional
public interface GenericService<T extends Identifiable> {

    default Optional<T> get(int id) {
        return getRepository().find(id);
    }

    default List<T> findAll() {
        return getRepository().findAll();
    }

    default T add(T entity) {
        if (getDuplicateChecker() != null) {
            getDuplicateChecker().accept(entity);
        }
        getRepository().add(entity);
        return get(entity.getId()).get();
    }

    default Optional<T> update(T entity) {
        getRepository().find(entity.getId())
                .orElseThrow(() -> new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND));
        if (getDuplicateChecker() != null) {
            getDuplicateChecker().accept(entity);
        }
        return getRepository().update(entity);
    }

    default void delete(int id) {
        T target = getRepository().find(id)
                .orElseThrow(() -> new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND));
        getRepository().delete(target);
    }

    default Consumer<T> getDuplicateChecker() {
        return null;
    }

    GenericRepository<T> getRepository();
}
