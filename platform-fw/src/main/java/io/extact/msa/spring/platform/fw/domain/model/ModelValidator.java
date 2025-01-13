package io.extact.msa.spring.platform.fw.domain.model;

import jakarta.validation.groups.Default;

/**
 * {@link EntityModel}に対するバリデーションインターフェース。
 * バリデーションエラーが発生した場合はエラー情報を設定した{@code} RmsValidationException}を返す。
 */
public interface ModelValidator {

    /**
     * {@link EntityModel} に定義されている{@code jakarta.validation.Constraint}に対する
     * バリデーションを実行する。
     *
     * @param model モデル
     * @param groups バリデーショングループ
     */
    void validateModel(DomainModel model, Object... groups);

    /**
     * {@link EntityModel} に定義されている{@code jakarta.validation.Constraint}に対する
     * バリデーションを実行する。
     *
     * @param model モデル
     */
    default void validateModel(DomainModel model) {
        this.validateModel(model, Default.class);
    }

    /**
     * 引数で指定された{@link EntityModel} のフィールドに対してのみバリデーションを行う。
     *
     * validateFieldはvalidateModelと違い、ネストオブジェクトのフィールドを再帰的にvalidateしていく
     * ことはなしない。よって、フィールドに@Validを付けている場合も、ネストオブジェクトのフィールドも
     * 以下のように明示的にvalidateする必要がある。
     *
     * <pre>
     * validator.validateField(model, "nestObject");
     * validator.validateField(model, "nestObject.field1");
     * validator.validateField(model, "nestObject.field2");
     * </pre>
     *
     * またvalidateはエラーを最初に検知したvalidateメソッドで例外が送出され中断されるためvalidateFieldで
     * 複数項目に対してエラーが発生することはない
     *
     * @param model モデル
     * @param targetField バリデーションするフィールド名
     * @param groups バリデーショングループ
     */
    void validateField(DomainModel model, String targetField, Object... groups);

    /**
     * 引数で指定された{@link EntityModel} のフィールドに対してのみバリデーションを行う。
     *
     * @param model モデル
     * @param targetField バリデーションするフィールド名
     * @param groups バリデーショングループ
     */
    default void validateField(DomainModel model, String targetField) {
        this.validateField(model, targetField, Default.class);
    }
}
