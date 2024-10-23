package io.extact.msa.spring.platform.fw.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.extact.msa.spring.platform.core.condition.SkipRegistration;
import io.extact.msa.spring.platform.core.stopbugs.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;

@RestController
@SkipRegistration
@Slf4j
public class ApplicationStopController {

    @GetMapping("stop")
    @SuppressFBWarnings("DM_EXIT")
    public String stopApplication(@RequestHeader("Host") String host) {

        if (!host.toLowerCase().startsWith("localhost")) {
            log.warn("Ignore because it is a request from other than localhost.[host={}]", host);
            return "failed";
        }

        new Thread( () -> {
            log.info("Receive end event: Ends after 3 seconds");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            log.info("Execute: System.exit(0)");
            System.exit(0);
        } ).start();

        return "success";
    }
}
