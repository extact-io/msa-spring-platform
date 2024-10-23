package io.extact.msa.spring.platform.core.auth.testapp.client;

public interface TestClient {

    ClientAuthData authenticate(String loginId, String password);

    boolean memeberApi();

    boolean adminApi();

    boolean guestApi();

    boolean guestApiWithLogin();
}
