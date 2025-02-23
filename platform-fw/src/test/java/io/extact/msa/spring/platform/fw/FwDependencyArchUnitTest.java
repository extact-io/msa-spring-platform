package io.extact.msa.spring.platform.fw;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.spring.platform.fw", importOptions = ImportOption.DoNotIncludeTests.class)
class FwDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // platform.fwパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * application, domain, exceptionパッケージで依存OKなライブラリの定義。
     * Java標準以外に依存しないクリーナ状態であること
     */
    @ArchTest
    static final ArchRule test_applicaiton_domain_exceptionで依存してOKなライブラリの定義 = classes()
            .that().resideInAnyPackage(
                    "..application..",
                    "..domain..",
                    "..exception..")
            .should().onlyDependOnClassesThat(resideInAnyPackage(
                    "io.extact.msa.spring.platform.core..",
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
}
