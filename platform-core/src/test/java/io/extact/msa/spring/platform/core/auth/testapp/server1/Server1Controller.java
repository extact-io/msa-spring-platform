package io.extact.msa.spring.platform.core.auth.testapp.server1;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.extact.msa.spring.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.spring.platform.fw.web.ExceptionHandled;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/server1")
@ExceptionHandled
@RequiredArgsConstructor
public class Server1Controller {

    private final Server1Assert server1Assert;
    private final Server2Api server2Api;

    @GenerateToken
    @GetMapping("/login") // 認証なし
    public ServerAuthData authenticate(@RequestParam("loginId") String loginId,
            @RequestParam("password") String password) {
        server1Assert.doBeforeLoginAssert();
        return new ServerAuthData(loginId, Set.of(password));
    }

    @GetMapping("/member") // 認証あり(member-role)
    public boolean memeberApi() {
        server1Assert.doMemberApiAssert();
        server2Api.memberLoginApi(); // Server2へのREST呼び出し
        server1Assert.doMemberApiAssert();
        return true;
    }

    @GetMapping("/admin") // 認証あり(admin-role)
    public boolean adminApi() {
        server1Assert.doAdminApiAssert();
        server2Api.adminLoginApi(); // Server2へのREST呼び出し
        server1Assert.doAdminApiAssert();
        return true;
    }

    @GetMapping("/guest") // 認証なし
    public boolean guestApi() {
        server1Assert.doGuestApiAssert();
        server2Api.notLoginApi();
        return true;
    }

    @GetMapping("/guest-with-login") // 認証なし(ログイン状態で呼び出し)
    public boolean guestApiWithLogin() {
        server1Assert.doGuestApiWithLoginAssert();
        return true;
    }
}
