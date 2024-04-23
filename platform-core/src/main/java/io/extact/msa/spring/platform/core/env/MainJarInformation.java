package io.extact.msa.spring.platform.core.env;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public class MainJarInformation {

    static final String INIT_VALUE = "-";

    @Builder.Default
    private String applicationName = INIT_VALUE;
    @Builder.Default
    private String jarName = INIT_VALUE;
    @Builder.Default
    private String mainClassName = INIT_VALUE;
    @Builder.Default
    private String version = INIT_VALUE;
    @Builder.Default
    private String buildTimeInfo = INIT_VALUE;

    public String startupModuleInfo() {
        if (applicationName.equals(INIT_VALUE)) {
            return INIT_VALUE;
        }
        return applicationName + "/" + jarName + "/" + mainClassName;
    }
}
