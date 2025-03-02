package io.extact.msa.spring.test.archunit;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.*;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Configuration;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;

public class ArchUnitUtils {

    public static DescribedPredicate<JavaClass> configurationClasses() {
        return belongTo(annotatedWith(Configuration.class)
                .or(annotatedWith(SpringBootConfiguration.class)));
    }

}
