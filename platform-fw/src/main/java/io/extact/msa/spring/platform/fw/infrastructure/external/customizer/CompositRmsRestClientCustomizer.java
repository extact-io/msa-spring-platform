package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositRmsRestClientCustomizer implements RmsRestClientCustomizer {

    private final List<RmsRestClientCustomizer> customizers;

    @Override
    public void customize(org.springframework.web.client.RestClient.Builder builder,
            RmsRestClientCustomizerContext context) {
        customizers.forEach(customizer -> customizer.customize(builder, context));
    }

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private List<RmsRestClientCustomizer> customizers = new ArrayList<>();

        public Builder add(RmsRestClientCustomizer customizer) {
            customizers.add(customizer);
            return this;
        }

        public CompositRmsRestClientCustomizer build() {
            return new CompositRmsRestClientCustomizer(this.customizers);
        }
    }
}
