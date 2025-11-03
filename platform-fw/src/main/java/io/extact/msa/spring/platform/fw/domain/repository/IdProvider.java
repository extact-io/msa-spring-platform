package io.extact.msa.spring.platform.fw.domain.repository;

import io.extact.msa.spring.platform.fw.domain.model.Identity;

public interface IdProvider<I extends Identity> {

    /**
     * 次のIDを発番する。
     *
     * @return 次のID
     */
    I nextIdentity();
}
