package io.extact.msa.spring.platform.fw.feature.auth;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

/**
 * フレームワーク自身がDBアクセスに利用するJdbcTemplate設定。
 * アプリの接続をprimary(デフォルト)としているため、フレームワークが使う接続は
 * secondaryにしている。よってBeanのアクセスにはすべてQualifireを付けて行う
 */
@Configuration(proxyBeanMethods = false)
public class FrameworkDataSourceConfig {

    @Bean
    @FrameworkDataSource
    @ConfigurationProperties("rms.datasource.fw")
    DataSourceProperties fwDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @FrameworkDataSource
    @ConfigurationProperties("rms.datasource.fw.hikari")
    DataSource fwDataSource(@FrameworkDataSource DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    JdbcTemplate fwJdbcTemplate(@FrameworkDataSource DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
