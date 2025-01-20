package io.extact.msa.spring.platform.fw;

import io.extact.msa.spring.platform.core.env.ActiveProfileResolver;
import io.extact.msa.spring.platform.core.env.MainModuleInformation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartupLog {

    public static void startupLog(MainModuleInformation info, ActiveProfileResolver apr) {
        log.info("Main Jar Information => " + System.lineSeparator() +
                "\tStartup-Module:" + info.jarName() + System.lineSeparator() +
                "\tVersion:" + info.version() + System.lineSeparator() +
                "\tBuild-Time:" + info.buildTIme()
                );
        log.info("Resolved Active Profiles => " + String.join(", ", apr.resolveActiveProfiles()));
    }
}
