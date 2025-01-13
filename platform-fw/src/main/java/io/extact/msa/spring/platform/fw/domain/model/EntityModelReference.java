package io.extact.msa.spring.platform.fw.domain.model;

/**
 * Domainクラスのinterface層公開用インタフェース。
 * interface層からDomainクラスへの参照はモデル変換を不要とするため許容しているが
 * Domainクラスの状態を変更する操作は制限したいため、このインタフェースを設けいている。
 * よって、interface層からDomainクラス本体への参照は禁止とする。
 * ただし、状態を変更する操作を持たない{@link ValueModel}は公開することによりデメリットは
 * ないため、そのまま公開して良いものとする。
 */
public interface EntityModelReference extends Identifiable, Transformable, Comparable<EntityModelReference> {
    @Override
    default int compareTo(EntityModelReference other) {
        return getId().compareTo(other.getId());
    }
}
