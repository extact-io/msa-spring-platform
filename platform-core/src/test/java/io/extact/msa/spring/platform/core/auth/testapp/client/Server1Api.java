package io.extact.msa.spring.platform.core.auth.testapp.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

//// test-classesは自動でRestClientインタフェースが検出されないので@AddBeanでインタフェースを登録すること
//@RegisterRestClient(configKey = "web-api")
////@RegisterProvider(RmsTypeParameterFeature.class)
//@RegisterProvider(PropagateResponseExceptionMapper.class)
//@RegisterProvider(CustomizableClientRequest.class)
//@RegisterClientHeaders(BearerTokenRequestInitializer.class)
//@Path("/server1")
@HttpExchange("/server1")
public interface Server1Api {

    @GetExchange("/login") // 認証なし
    ResponseEntity<ClientAuthData> authenticate(@RequestParam("loginId") String loginId,
            @RequestParam("password") String password);

    @GetExchange("/member") // 認証あり(member-role)
    boolean memeberApi();

    @GetExchange("/admin") // 認証あり(admin-role)
    boolean adminApi();

    @GetExchange("/guest") // 認証なし
    boolean guestApi();

    @GetExchange("/guest-with-login") // 認証なし(ログイン状態で呼び出し)
    boolean guestApiWithLogin();
}
