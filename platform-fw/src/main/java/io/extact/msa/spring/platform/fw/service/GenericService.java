package io.extact.msa.spring.platform.fw.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import io.extact.msa.spring.platform.fw.domain.Identity;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;

@Transactional
public interface GenericService<T extends DomainModel> {

    default Optional<T> get(Identity id) {
        return getRepository().find(id);
    }

    default List<T> findAll() {
        return getRepository().findAll();
    }

    /**
     * 追加
     * @param command
     * @return モデル（発番された番号を返す必要がある）
     */
    default T add(T command) {
        if (getDuplicateChecker() != null) {
            getDuplicateChecker().accept(command);
        }
        getRepository().add(command);
        return get(command.getId()).get();
    }

    // 更新対象がなかったら例外なので、常に更新後の結果は変える
    /**
     * 更新
     * @param entity
     * @return 更新結果。更新対象がない場合は例外となるので結果は常に変える
     * @throws BusinessFlowException 更新対象が存在しない場合。
     */
    default T update(T entity) {
        getRepository().find(entity.getId())
                .orElseThrow(() -> new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND));
        if (getDuplicateChecker() != null) {
            getDuplicateChecker().accept(entity);
        }
        getRepository().update(entity);
        return entity;
    }

    default void delete(Identity id) {
        T target = getRepository().find(id)
                .orElseThrow(() -> new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND));
        getRepository().delete(target);
    }

    default Consumer<T> getDuplicateChecker() {
        return null;
    }

    GenericRepository<T> getRepository();
}
