package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import org.springframework.web.client.RestClient.Builder;

import io.extact.msa.spring.platform.core.auth.client.BearerTokenRequestInitializer;
import io.extact.msa.spring.platform.core.auth.client.LoginUserHeaderRequestInitializer;

public class BearerTokenRequestInitializerCustomizer implements RmsRestClientCustomizer {

    public static final BearerTokenRequestInitializerCustomizer INSTANCE = new BearerTokenRequestInitializerCustomizer();

    @Override
    public void customize(Builder builder, RmsRestClientCustomizerContext context) {
        builder.requestInitializers(initializers -> {
                    initializers.removeIf(LoginUserHeaderRequestInitializer.class::isInstance); // defaultで入っているのを削除
                    initializers.add(new BearerTokenRequestInitializer());
                });
    }
}
