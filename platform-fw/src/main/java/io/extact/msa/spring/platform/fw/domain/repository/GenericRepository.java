package io.extact.msa.spring.platform.fw.domain.repository;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;

/**
 * 永続先に依らないリポジトリの共通操作
 *
 * @param <M> ドメインモデルの型
 * @param <I> ドメインモデルのIDの型
 */
public interface GenericRepository<M extends EntityModel> {

    /**
     * IDのエンティティを取得する。
     *
     * @param id ID
     * @return エンティティ。該当なしはnull
     */
    Optional<M> find(Identity id);

    /**
     * 永続化されているエンティティを全件取得する
     *
     * @return エンティティの全件リスト。該当なしは空リスト
     */
    List<M> findAll();

    /**
     * エンティティを追加する。
     * {@link Valid}によりオブジェクトのValidationが実行される。
     *
     * @param model エンティティ
     */
    void add(M model);

    /**
     * エンティティを更新する。
     * {@link Valid}によりオブジェクトのValidationが実行される。
     *
     * @param model 更新内容
     * @return 更新後エンティティ。更新対象が存在しない場合はnull
     */
    void update(M model);

    /**
     * エンティティを削除する。
     *
     * @param model 削除エンティティ
     */
    void delete(M model);
}
