package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.Optional;

import org.springframework.web.client.RestClient.Builder;

import io.extact.msa.spring.platform.core.auth.client.BearerTokenRequestInitializer;
import io.extact.msa.spring.platform.core.auth.client.LoginUserHeaderRequestInitializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizer;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizerContext;

public class BearerTokenRequestInitializerCustomizer implements RmsRestClientCustomizer {

    public static final BearerTokenRequestInitializerCustomizer INSTANCE = new BearerTokenRequestInitializerCustomizer();

    public final Optional<RmsRestClientCustomizer> next;

    public BearerTokenRequestInitializerCustomizer() {
        this(null);
    }
    public BearerTokenRequestInitializerCustomizer(RmsRestClientCustomizer next) {
        this.next = Optional.ofNullable(next);
    }

    @Override
    public void customize(Builder builder, RmsRestClientCustomizerContext context) {
        builder.requestInitializers(initializers -> {
                    initializers.removeIf(LoginUserHeaderRequestInitializer.class::isInstance); // defaultで入っているのを削除
                    initializers.add(new BearerTokenRequestInitializer());
                });
        next.ifPresent(customizer -> customizer.customize(builder, context));
    }
}
