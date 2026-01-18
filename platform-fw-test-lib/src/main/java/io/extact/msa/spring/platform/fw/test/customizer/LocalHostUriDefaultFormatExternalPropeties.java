package io.extact.msa.spring.platform.fw.test.customizer;

import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;

/**
 * ExternalProperties経由でLocalHostUriBuilderFactoryと同じことをしたい場合に
 * 利用するプロパティクラス。フォーマットにはrmsのデフォルトを設定している。
 */
public class LocalHostUriDefaultFormatExternalPropeties extends ExternalProperties {

    public LocalHostUriDefaultFormatExternalPropeties(Environment env) {

        String date = env.resolvePlaceholders("${spring.mvc.format.date}"); // default format
        String datetime = env.resolvePlaceholders("${spring.mvc.format.date-time}"); // default format

        FormatProperties props = new FormatProperties();
        props.setDate(date);
        props.setDateTime(datetime);
        this.setFormat(props);

        // UriBuilderに遅延でresolveされるのこの段階でのplaceholderの解決は不要
        this.setUrl("http://localhost:${local.server.port}");
    }
}
