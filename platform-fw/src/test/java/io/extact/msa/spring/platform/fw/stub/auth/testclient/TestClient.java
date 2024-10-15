package io.extact.msa.spring.platform.fw.stub.auth.testclient;

public interface TestClient {

    ClientAuthData authenticate(String loginId, String password);

    boolean memeberApi();

    boolean adminApi();

    boolean guestApi();

    boolean guestApiWithLogin();
}
