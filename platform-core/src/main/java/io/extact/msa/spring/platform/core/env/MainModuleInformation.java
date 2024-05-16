package io.extact.msa.spring.platform.core.env;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

/**
 * アプリケーションを起動するmainメソッドを持つExecutable-Jarモジュールの情報クラス。
 * このクラスはクラスパス上のbuild-info.propertiesとgit.propertiesファイルをもとに生成される。
 * <p>
 * Executable-Jar以外にbuild-info.propertiesとgit.propertiesファイルが存在すると誤った情報を
 * 取得するため、build-info.propertiesとgit.propertiesファイルを生成する以下のplugin設定は
 * Executable-Jarプロジェクトのみに設定すること。
 *
 * <pre>
 *   <!-- generate /MEATA-INF/build.proeprties -->
 *   <plugin>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-maven-plugin</artifactId>
 *     <executions>
 *       <execution>
 *         <goals>
 *           <goal>build-info</goal>
 *         </goals>
 *       </execution>
 *     </executions>
 *   </plugin>
 *   <!-- generate /MEATA-INF/git.proeprties -->
 *   <plugin>
 *     <groupId>io.github.git-commit-id</groupId>
 *     <artifactId>git-commit-id-maven-plugin</artifactId>
 *     <executions>
 *       <execution>
 *         <goals>
 *           <goal>revision</goal>
 *         </goals>
 *       </execution>
 *     </executions>
 *     <configuration>
 *       ..
 *     </configuration>
 *   </plugin>
 * </pre>
 */
@Slf4j
public class MainModuleInformation {

    private Optional<String> applicationName;
    private Optional<BuildProperties> buildProperties;
    private Optional<GitProperties> gitProperties;

    private static final String UNKNOWN_VALUE = "-";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public MainModuleInformation(Environment env, BuildProperties buildProperties, GitProperties gitProperties) {
        this.applicationName = Optional.ofNullable(env.getProperty("spring.application.name"));
        this.buildProperties = Optional.ofNullable(buildProperties);
        this.gitProperties = Optional.ofNullable(gitProperties);
    }

    public String applicationName() {
        return applicationName.orElseGet(() -> {
            log.warn("spring.application.name property not set.");
            return UNKNOWN_VALUE; // fallback
        });
    }

    public String jarName() {
        return buildProperties
                .map(prop -> prop.getArtifact() + ".jar")
                .orElse(UNKNOWN_VALUE);
    }

    public String version() {
        return buildProperties
                .map(BuildProperties::getVersion)
                .orElse(UNKNOWN_VALUE);
    }

    public String commitId() {
        return gitProperties
                .map(GitProperties::getShortCommitId)
                .orElse(UNKNOWN_VALUE);
    }

    public String buildTIme() {
        return buildProperties
                .map(BuildProperties::getTime)
                .map(this::formatZonedDatatime)
                .orElse(UNKNOWN_VALUE);
    }

    public String moduleInfo() {
        return applicationName() + ":" + jarName();
    }

    public String versionInfo() {
        return version() + ":" + commitId();
    }

    private String formatZonedDatatime(Instant instant) {
        ZonedDateTime zonedDatetime = instant.atZone(ZoneId.systemDefault());
        return zonedDatetime.format(FORMAT);
    }
}
