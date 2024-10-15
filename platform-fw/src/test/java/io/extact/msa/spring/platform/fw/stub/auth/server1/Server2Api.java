package io.extact.msa.spring.platform.fw.stub.auth.server1;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

//test-classesは自動でRestClientインタフェースが検出されないので@AddBeanでインタフェースを登録すること
//@RegisterRestClient(configKey = "web-api")
//@RegisterProvider(RmsTypeParameterFeature.class)
//@RegisterProvider(PropagateResponseExceptionMapper.class)
//@RegisterClientHeaders(LoginUserHeaderRequestInitializer.class)
@HttpExchange("/server2")
public interface Server2Api {

    @GetExchange("/not-login")
    boolean notLoginApi();

    @GetExchange("/member-login")
    boolean memberLoginApi();

    @GetExchange("/admin-login")
    boolean adminLoginApi();
}
