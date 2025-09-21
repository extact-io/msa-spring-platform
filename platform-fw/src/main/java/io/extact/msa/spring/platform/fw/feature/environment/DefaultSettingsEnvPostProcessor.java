package io.extact.msa.spring.platform.fw.feature.environment;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.InputStreamResource;

public class DefaultSettingsEnvPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try (var is = getClass().getResourceAsStream("/default-settings.yml")) {
            if (is == null) {
                return;
            }
            PropertySourceLoader loader = new YamlPropertySourceLoader();
            List<PropertySource<?>> sources = loader.load("library-default", new InputStreamResource(is));
            sources.forEach(ps -> environment.getPropertySources().addLast(ps));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
    }
}
