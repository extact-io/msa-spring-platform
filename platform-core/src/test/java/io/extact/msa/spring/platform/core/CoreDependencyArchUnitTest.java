package io.extact.msa.spring.platform.core;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import jakarta.annotation.PostConstruct;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.spring.platform.core", importOptions = ImportOption.DoNotIncludeTests.class)
class CoreDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // パッケージの内部構造の検証
    // ---------------------------------------------------------------------

    /**
     * jose4jとAuth0への依存はprovider.implパッケージのみの定義
     * <pre>
     * ・jose4jとAuth0へはprovider.implパッケージでしか依存していないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_JWT実装への依存はimplパッケージのみの定義 = noClasses()
            .that()
            .resideOutsideOfPackage("..jwt.encode.impl..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "org.jose4j..",
                    "com.auth0.jwt..");

    /**
     * jwtパッケージの依存関係の定義
     * <pre>
     * ・jwtパッケージ直下のクラスはjwtの実装依存のimplパッケージに依存してないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_jwtパッケージ内部の依存関係の定義 = noClasses()
            .that()
            .resideInAPackage("..jwt.encode")
            .and().resideOutsideOfPackage("..jwt.encode.impl..")
            .and().doNotHaveSimpleName("JwtEncodeConfig")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..jwt.encode.impl..");

    /**
     * jakarta.servletへの依存はcore.authパッケージのみの定義
     * <pre>
     * ・servletへはcore.authjパッケージでしか依存していないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_servletへの依存はauthパッケージのみの定義 = noClasses()
            .that()
            .resideOutsideOfPackage("..auth..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("jakarta.servlet..");

    /**
     * Logback-Accessへの依存はcore.logパッケージのみの定義
     * <pre>
     * ・Logback-Accessへはcore.logパッケージでしか依存していないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_logbackaccessへの依存はauthパッケージのみの定義 = noClasses()
            .that()
            .resideOutsideOfPackage("..log..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("ch.qos.logback.access.tomcat..");

    // ---------------------------------------------------------------------
    // パッケージごとの依存可能モジュールの検証
    // ---------------------------------------------------------------------

    /**
     * core全体で依存OKなライブラリの定義。
     */
    @ArchTest
    static final ArchRule test_coreで依存してOKなライブラリの定義 = classes()
            .that().resideOutsideOfPackages(
                    "..jwt.encode.impl..",
                    "..auth..",
                    "..log..")
            .should().onlyDependOnClassesThat(
                    resideInAnyPackage(
                            "io.extact.msa.spring.platform.core..",
                            "java..",
                            "jakarta.validation..",
                            "org.springframework..",
                            "org.slf4j..",
                            "org.aspectj..",
                            "io.micrometer..",
                            "lombok..")
                                    .or(type(PostConstruct.class)));

    /**
     * genericパッケージで依存OKなライブラリの定義。
     * genericパッケージはアプリケーションコアから依存を許可しているためgenericパッケージも
     * クリーンに保つ必要がある
     */
    @ArchTest
    static final ArchRule test_genericで依存してOKなライブラリの定義 = classes()
            .that().resideInAPackage("..generic..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("java..");
}
