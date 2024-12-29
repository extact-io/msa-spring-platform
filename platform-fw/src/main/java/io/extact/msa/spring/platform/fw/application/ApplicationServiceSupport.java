package io.extact.msa.spring.platform.fw.application;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;

/**
 * ApplicaitonServiceの共通的なメソッドを定義したインターフェース。
 * ApplicaitonServiceがこのインタフェースを実装するかは任意としている。
 *
 * @param <M> Modelクラス
 */
public interface ApplicationServiceSupport<M extends DomainModel> {

    @Transactional
    default Optional<M> getById(Identity id) {
        return getRepository().find(id);
    }

    @Transactional
    default List<M> getAll() {
        return getRepository().findAll();
    }

    @Transactional
    default void delete(Identity id) {
        M target = getRepository().find(id)
                .orElseThrow(() -> new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND));
        getRepository().delete(target);
    }

    GenericRepository<M> getRepository();
}
