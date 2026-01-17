package io.extact.msa.spring.platform.fw.infrastructure.external;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import lombok.Builder;

/**
 * RestClient利用時のURL変換に独自のConversationServiceを使いたい場合に
 * 利用するUriBuilderFactory実装。
 * このUriBuilderFactoryが設定されたRestClientを元にHttpInterfaceを作って
 * もURLの変換にはここで設定したConversationServiceが使われることはなく、
 * HttpInterfaceに設定したConversationServiceが使われるため注意すること。
 */
@Builder(builderMethodName = "newInstance", builderClassName = "Builder")
public class CustomUriBuilderFactory extends DefaultUriBuilderFactory {

    private Environment env;
    private String baseUri;
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
        return env.resolveRequiredPlaceholders(this.baseUri);
    }
}
