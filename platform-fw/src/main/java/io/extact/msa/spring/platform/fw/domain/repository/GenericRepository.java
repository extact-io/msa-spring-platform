package io.extact.msa.spring.platform.fw.domain.repository;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;

import org.springframework.validation.annotation.Validated;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Delete;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;

/**
 * 永続先に依らないリポジトリの共通操作
 *
 * @param <M> ドメインモデルの型
 */
@Validated // メソッドバリデーションを有効にするための@Validated
public interface GenericRepository<M extends DomainModel> {

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
    @Validated({ Default.class, Add.class }) // グループを指定するための@Validated
    void add(@Valid M model); // 引数にバリデーションを掛けるための@Valid(この@Validがないとバリデーションは実行されない)

    /**
     * エンティティを更新する。
     * {@link Valid}によりオブジェクトのValidationが実行される。
     *
     * @param model 更新内容
     * @return 更新後エンティティ。更新対象が存在しない場合はnull
     */
    @Validated({ Default.class, Update.class })
    void update(@Valid M model);

    /**
     * エンティティを削除する。
     *
     * @param model 削除エンティティ
     */
    @Validated({ Default.class, Delete.class })
    void delete(@Valid M model);

    /**
     * 次のIDを発番する。
     *
     * @return 次のID
     */
    int nextIdentity();
}
