package io.extact.msa.spring.platform.fw.domain.model;

/**
 * EntityModelの同値性はIDのみで確認する一方、EntityModelViewの場合はすべての値が等しいかで比較
 * したい場合がある。そのような場合はこのインタフェースを実装して同値性の判断を個別に実装する。
 */
@FunctionalInterface
public interface IsEqualable<T> {
    boolean isEqual(T other);
}
