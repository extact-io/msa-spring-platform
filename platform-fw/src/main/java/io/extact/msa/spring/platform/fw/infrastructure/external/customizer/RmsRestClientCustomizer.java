package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import org.springframework.core.convert.ConversionService;
import org.springframework.web.client.RestClient;

@FunctionalInterface
public interface RmsRestClientCustomizer {

    void customize(RestClient.Builder builder, RmsRestClientCustomizerContext context);

    default ConversionService appliedConversionService() {
        return null;
    }
}
