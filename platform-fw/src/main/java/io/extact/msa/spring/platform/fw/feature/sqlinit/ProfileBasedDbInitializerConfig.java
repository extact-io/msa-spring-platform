package io.extact.msa.spring.platform.fw.feature.sqlinit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.StringUtils;

/**
 * @see org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
 * @see org.springframework.boot.autoconfigure.sql.init.DataSourceInitializationConfiguration
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.sql.init", name = "enabled", matchIfMissing = true)
@Import(DatabaseInitializationDependencyConfigurer.class)
public class ProfileBasedDbInitializerConfig {

    @Bean("profile-based") // SqlInitializationAutoConfigurationでも登録されるためBean名で取得できるようにしている
    @ConfigurationProperties("rms.persistence.sql.init")
    SqlInitializationProperties sqlInitializationProperties() {
        return new SqlInitializationProperties();
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer profileBasedDataSourceScriptInitializer(
            DataSource dataSource,
            @Qualifier("profile-based") SqlInitializationProperties properties,
            Environment env) {

        return new ProfileBasedDbInitializer(
                determineDataSource(
                        dataSource,
                        properties.getUsername(),
                        properties.getPassword()),
                createSettings(
                        env,
                        properties));
    }

    private static DataSource determineDataSource(DataSource dataSource, String username, String password) {
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            return DataSourceBuilder.derivedFrom(dataSource)
                    .username(username)
                    .password(password)
                    .type(SimpleDriverDataSource.class)
                    .build();
        }
        return dataSource;
    }

    private static DatabaseInitializationSettings createSettings(Environment env,
            SqlInitializationProperties properties) {

        return ProfileBasedDbInitializerSettingsCreator.create(env, properties);
    }
}
