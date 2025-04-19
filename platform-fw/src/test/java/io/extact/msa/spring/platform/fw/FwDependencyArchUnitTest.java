package io.extact.msa.spring.platform.fw;

import static com.tngtech.archunit.base.DescribedPredicate.*;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;
import static io.extact.msa.spring.test.archunit.ArchUnitUtils.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@AnalyzeClasses(packages = "io.extact.msa.spring.platform.fw", importOptions = ImportOption.DoNotIncludeTests.class)
class FwDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // アーキテクチャールールの検証
    // ---------------------------------------------------------------------

    /**
     * オニオンアーキテクチャが遵守されているかの検証。
     * @see https://www.archunit.org/userguide/html/000_Index.html#_onion_architecture
     */
    @ArchTest
    static final ArchRule architecture_respect_onion = onionArchitecture()
            .domainModels(
                    "..domain.model..",
                    "..domain.constraint..",
                    "..exception..")
            .domainServices(
                    "..domain.event..",
                    "..domain.service..",
                    "..domain.repository..")
            .applicationServices(
                    "..application..")
            // それぞれのapapterは独立し、相互に依存関係がないこともチェックされる
            .adapter("interface-webapi", "..interfaces", "..interfaces.webapi..")
            .adapter("persistence", "..infrastructure.persistence..") // さらにslicesで個別に独立性をチェック
            .adapter("external", "..infrastructure.external..")
            // Cofigurationクラスからの依存はすべて無視する
            // featureパッケージからdomainへの依存は許容
            .ensureAllClassesAreContainedInArchitectureIgnoring(configurationClasses())
            .ensureAllClassesAreContainedInArchitectureIgnoring(resideInAnyPackage("..feature.."))
            .ignoreDependency(
                    configurationClasses(),
                    alwaysTrue())
            .ignoreDependency(
                    resideInAnyPackage("..feature.."),
                    resideInAnyPackage(
                            "..application..",
                            "..domain.event..",
                            "..domain.model..",
                            "..exception.."));

    /**
     * persistence配下のパッケージ(file/jpa/remote)は独立し相互に依存していないこと。
     * <p>
     * ・fileパッケージがjpaパッケージを利用しているといったことがないこと
     */
    @ArchTest
    static final ArchRule isolate_persistence_not_depend_on_each_other = SlicesRuleDefinition.slices()
            .matching("..infrastructure.persistence.(*)..")
            .should().notDependOnEachOther();

    // ---------------------------------------------------------------------
    // platform.fwパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * アプリケーションコア(application, domain, exceptionパッケージ)で依存OKなライブラリの定義。
     * Java標準以外に依存しないクリーンな状態であること。
     * なお、{@link #architecture_respect_onion} でfw内の不要な他のパッケージに依存していないことが
     * 確認されていることを前提に検証している。また、core.genericパッケージもクリーンになっている
     */
    @ArchTest
    static final ArchRule dependency_application_core = classes()
            .that().resideInAnyPackage(
                    "..application..",
                    "..domain..",
                    "..exception..")
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core.generic..",
                    "io.extact.msa.spring.platform.fw..",
                    "java..",
                    "jakarta.validation..",
                    "lombok..")
                            // Springへの依存はNGだが@Transactionalだけは許容
                            .or(type(org.springframework.transaction.annotation.Transactional.class))
                            .or(type(org.springframework.transaction.annotation.Propagation.class))
                            .or(type(org.springframework.transaction.annotation.Isolation.class))
                            .or(type(org.springframework.core.annotation.AliasFor.class))

            );

    /**
     * interfacesパッケージから依存してOKなモジュールの検証
     */
    @ArchTest
    static final ArchRule dependency_fw_interfaces = classes()
            .that().resideInAnyPackage("..interfaces..").and(not(configurationClasses()))
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core.generic..",
                    "io.extact.msa.spring.platform.core.env..",
                    "io.extact.msa.spring.platform.core.condition..",
                    "io.extact.msa.spring.platform.core.stopbugs..",
                    "io.extact.msa.spring.platform.fw.exception..",
                    "io.extact.msa.spring.platform.fw.feature.validator..",
                    "io.extact.msa.spring.platform.fw.feature.exception..",
                    "io.extact.msa.spring.platform.fw.interfaces..",
                    "org.springframework.core..",
                    "org.springframework.beans..",
                    "org.springframework.context..",
                    "org.springframework.http..",
                    "org.springframework.web..",
                    "java..",
                    "jakarta.validation..",
                    "org.slf4j..",
                    "lombok..")
                    // org.springframework.boot 全体への依存は粗すぎるのでクラス指定
                    .or(type(org.springframework.boot.CommandLineRunner.class))

            );

    /**
     * persistence.fileパッケージから依存してOKなモジュールの検証
     */
    @ArchTest
    static final ArchRule dependency_fw_dependency_persistence_file = classes()
            .that().resideInAnyPackage("..infrastructure.persistence.file..").and(not(configurationClasses()))
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core.generic..",
                    "io.extact.msa.spring.platform.fw.domain..",
                    "io.extact.msa.spring.platform.fw.exception..",
                    "io.extact.msa.spring.platform.fw.infrastructure.persistence.file..",
                    "io.extact.msa.spring.platform.fw.feature.exception..",
                    "org.springframework.core..",
                    "org.springframework.beans..",
                    "org.springframework.context..",
                    "java..",
                    "org.apache.commons.csv..", // CSV形式で可能のするのでOK
                    "org.slf4j..",
                    "lombok..") //
            );

    /**
     * persistence.jpaパッケージから依存してOKなモジュールの検証
     */
    @ArchTest
    static final ArchRule dependency_fw_dependency_persistence_jpa = classes()
            .that().resideInAnyPackage("..infrastructure.persistence.jpa..")
            .and().resideOutsideOfPackage("..infrastructure.persistence.jpa.hibernate..")
            .and(not(configurationClasses()))
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core.generic..",
                    "io.extact.msa.spring.platform.fw.domain..",
                    "io.extact.msa.spring.platform.fw.exception..",
                    "io.extact.msa.spring.platform.fw.infrastructure.persistence", // 直下
                    "io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa..",
                    "io.extact.msa.spring.platform.fw.feature.exception..",
                    "org.springframework.core..",
                    "org.springframework.beans..",
                    "org.springframework.context..",
                    "org.springframework.data..", // Spring dataなのでOK
                    "org.springframework.util..",
                    "java..",
                    "jakarta.persistence..",
                    "org.slf4j..",
                    "lombok..") //
            );

    /**
     * Hibernateへはpersistence.jpa.hibernateパッケージでしか依存していないこと
     * ・persistence.jpa.hibernateパッケージ以外にhibernateに依存しているクラスがないこと
     */
    @ArchTest
    static final ArchRule dependency_hibernate = noClasses()
            .that()
            .resideOutsideOfPackage("..infrastructure.persistence.jpa.hibernate..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.hibernate..");

    /**
     * persistence.remoteパッケージから依存してOKなモジュールの検証
     */
    @ArchTest
    static final ArchRule dependency_fw_dependency_persistence_remote = classes()
            .that().resideInAnyPackage("..infrastructure.persistence.remote..")
            .and(not(configurationClasses()))
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core.generic..",
                    "io.extact.msa.spring.platform.fw.domain..",
                    "io.extact.msa.spring.platform.fw.exception..",
                    "io.extact.msa.spring.platform.fw.infrastructure.persistence", // 直下
                    "io.extact.msa.spring.platform.fw.infrastructure.persistence.remote..",
                    "io.extact.msa.spring.platform.fw.feature.exception..",
                    "org.springframework.core..",
                    "org.springframework.beans..",
                    "org.springframework.context..",
                    "org.springframework.web..", // Spring webなのでOK
                    "org.springframework.util..",
                    "java..",
                    "org.slf4j..",
                    "lombok..") //
            );

    /**
     * externalパッケージから依存してOKなモジュールの検証
     */
    @ArchTest
    static final ArchRule dependency_fw_dependency_external = classes()
            .that().resideInAnyPackage("..infrastructure.external..")
            .and(not(configurationClasses()))
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core.generic..",
                    "io.extact.msa.spring.platform.fw.domain..",
                    "io.extact.msa.spring.platform.fw.exception..",
                    "io.extact.msa.spring.platform.fw.infrastructure.external..",
                    "io.extact.msa.spring.platform.fw.feature.exception..",
                    "org.springframework.core..",
                    "org.springframework.beans..",
                    "org.springframework.context..",
                    "org.springframework.web..", // Spring webなのでOK
                    "org.springframework.http..", // Spring webなのでOK
                    "org.springframework.util..",
                    "com.fasterxml.jackson..",
                    "java..",
                    "org.slf4j..",
                    "lombok..")
                            .or(type(org.springframework.format.support.DefaultFormattingConversionService.class)));

    /**
     * featureパッケージから依存してOKなモジュールの検証
     * ・featureパッケージで依存してOKなOSSはSpringのみであること
     */
    @ArchTest
    static final ArchRule dependency_fw_feature = classes()
            .that().resideInAnyPackage("..feature..")
            .and(not(configurationClasses()))
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core.env..",
                    "io.extact.msa.spring.platform.fw.application..",
                    "io.extact.msa.spring.platform.fw.domain..",
                    "io.extact.msa.spring.platform.fw.exception..",
                    "io.extact.msa.spring.platform.fw.feature..",
                    "org.springframework..", // Spring
                    "org.aspectj..", // for Interceptor
                    "java..",
                    "javax.sql..",
                    "org.slf4j..",
                    "lombok..") //
            );
}
