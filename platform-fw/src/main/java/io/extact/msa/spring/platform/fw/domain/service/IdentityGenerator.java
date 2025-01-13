package io.extact.msa.spring.platform.fw.domain.service;

public interface IdentityGenerator {

    /**
     * 次のIDを発番する。
     *
     * @return 次のID
     */
    int nextIdentity();
}
