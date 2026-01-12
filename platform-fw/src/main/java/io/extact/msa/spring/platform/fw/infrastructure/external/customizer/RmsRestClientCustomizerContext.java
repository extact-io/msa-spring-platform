package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;

public interface RmsRestClientCustomizerContext {

    ApplicationContext getApplicationContext();

    Optional<ConversionService> currentAppiedConversionService();
}
