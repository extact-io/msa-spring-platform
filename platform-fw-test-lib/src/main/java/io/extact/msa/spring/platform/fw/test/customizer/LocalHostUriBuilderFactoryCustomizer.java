package io.extact.msa.spring.platform.fw.test.customizer;

import org.springframework.web.client.RestClient.Builder;

import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizer;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizerContext;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

public class LocalHostUriBuilderFactoryCustomizer implements RmsRestClientCustomizer {

    public static final LocalHostUriBuilderFactoryCustomizer INSTANCE = new LocalHostUriBuilderFactoryCustomizer();

    @Override
    public void customize(Builder builder, RmsRestClientCustomizerContext context) {
        builder.uriBuilderFactory(new LocalHostUriBuilderFactory(context.getApplicationContext().getEnvironment()));
    }
}
