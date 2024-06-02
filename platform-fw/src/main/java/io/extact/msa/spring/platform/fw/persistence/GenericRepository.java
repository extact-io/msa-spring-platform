package io.extact.msa.spring.platform.fw.persistence;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Update;
import jakarta.validation.Valid;

/**
 * 永続先に依らないリポジトリの共通操作
 *
 * @param <T> エンティティの型
 */
@Validated
public interface GenericRepository<T> {

    /**
     * IDのエンティティを取得する。
     *
     * @param id ID
     * @return エンティティ。該当なしはnull
     */
    T get(int id);

    /**
     * 永続化されているエンティティを全件取得する
     *
     * @return エンティティの全件リスト。該当なしは空リスト
     */
    List<T> findAll();

    /**
     * エンティティを追加する。
     * {@link Valid}によりオブジェクトのValidationが実行される。
     *
     * @param entity エンティティ
     */
    @Validated(Add.class)
    void add(@Valid T entity);

    /**
     * エンティティを更新する。
     * {@link Valid}によりオブジェクトのValidationが実行される。
     *
     * @param entity 更新内容
     * @return 更新後エンティティ。更新対象が存在しない場合はnull
     */
    @Validated(Update.class)
    T update(@Valid T entity);

    /**
     * エンティティを削除する。
     *
     * @param entity 削除エンティティ
     */
    void delete(T entity);

    /**
     * コンフィグ定数
     */
    static class ApiType {
        public static final String PROP_NAME ="rms.persistence.apiType";
        public static final String FILE = "file";
        public static final String JPA = "jpa";
    }
}
