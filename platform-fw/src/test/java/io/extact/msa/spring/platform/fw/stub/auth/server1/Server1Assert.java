package io.extact.msa.spring.platform.fw.stub.auth.server1;

public interface Server1Assert {
    void doBeforeLoginAssert();
    void doMemberApiAssert();
    void doAdminApiAssert();
    void doGuestApiAssert();
    void doGuestApiWithLoginAssert();
}
