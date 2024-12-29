package io.extact.msa.spring.platform.fw.domain.model;

public interface Verifiable {
    /**
     * オブジェクト自身が持つ情報が正しいかを検証する。
     */
    void verify();
}
