package io.extact.msa.spring.platform.core.auth.testapp.server2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/server2")
@RequiredArgsConstructor
public class Server2Controller {

    private final Server2Assert server2Assert;

    @GetMapping("/not-login")
    public boolean notLoginApi() {
        server2Assert.doNotLoginApiAssert();
        return true;
    }

    @GetMapping("/member-login")
    public boolean memberLoginApi() {
        server2Assert.doMemberLoginApiAssert();
        return true;
    }

    @GetMapping("/admin-login")
    public boolean adminLoginApi() {
        server2Assert.doAdminLoginApi();
        return true;
    }
}
