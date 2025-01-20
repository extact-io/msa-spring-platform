package io.extact.msa.spring.platform.core.env;

import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActiveProfileResolver {

    private final Environment environment;

    public String[] resolveActiveProfiles() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            // activeProfiles が空の場合、defaultProfiles を使用
            activeProfiles = environment.getDefaultProfiles();
        }
        return activeProfiles;
    }
}
