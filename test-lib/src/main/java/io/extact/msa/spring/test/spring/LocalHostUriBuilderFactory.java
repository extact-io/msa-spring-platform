package io.extact.msa.spring.test.spring;

import org.springframework.core.env.Environment;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

/**
 * HttpInterceを利用する場合はUriBuilderFactoryに設定したConversionServiceは
 * 利用されない。よってHttpInterceを利用するためにRestClientを作る場合は、
 * 簡易的なこのクラスを使っても問題ない。
 * ただし、テストでRestClientを直接使う場合はCustomUriBuilderFactoryを使うこと。
 */
public class LocalHostUriBuilderFactory extends DefaultUriBuilderFactory {

    private Environment env;
    private String basePath;

    public LocalHostUriBuilderFactory(Environment env) {
        this(env, "");
    }

    public LocalHostUriBuilderFactory(Environment env, String basePath) {
        this.env = env;
        this.basePath = basePath;
    }

    // UriBuilderFactory

    @Override
    public UriBuilder uriString(String uriTemplate) {
        return super.uriString(localhostUriTemplate() + uriTemplate);
    }

    @Override
    public UriBuilder builder() {
        return super.uriString(localhostUriTemplate());
    }

    private String localhostUriTemplate() {
        return "http://localhost:" + env.getProperty("local.server.port") + basePath;
    }
}
