package io.extact.msa.spring.platform.fw.domain.model;

/**
 * Domainクラスのinterface層公開用インタフェース。
 * interface層からDomainクラスへの参照はモデル変換を不要とするため許容しているが
 * Domainクラスの状態を変更する操作は制限したいため、このインタフェースを設けいている。
 * よって、interface層からDomainクラス本体への参照は禁止とする。
 */
public interface ReferenceModel extends Identifiable, Transformable, Comparable<ReferenceModel> {
    @Override
    default int compareTo(ReferenceModel other) {
        return getId().compareTo(other.getId());
    }
}
