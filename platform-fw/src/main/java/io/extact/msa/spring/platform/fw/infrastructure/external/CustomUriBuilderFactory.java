package io.extact.msa.spring.platform.fw.infrastructure.external;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import lombok.Builder;

@Builder(builderMethodName = "newInstance")
public class CustomUriBuilderFactory extends DefaultUriBuilderFactory {

    private Environment env;
    private String uriTemplate;
    private ConversionService conversionService;

    @Override
    public UriBuilder uriString(String uriTemplate) {
        String baseUriTemplate = resolveTemplate() + uriTemplate;
        UriBuilder original = super.uriString(baseUriTemplate);
        return new CustomUriBuilder(original, conversionService);
    }

    @Override
    public UriBuilder builder() {
        return this.uriString(resolveTemplate());
    }

    private String resolveTemplate() {
        return env.resolveRequiredPlaceholders(this.uriTemplate);
    }
}
