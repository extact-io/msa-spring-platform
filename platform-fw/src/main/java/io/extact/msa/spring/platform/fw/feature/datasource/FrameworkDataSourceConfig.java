package io.extact.msa.spring.platform.fw.feature.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * フレームワーク自身がDBアクセスに利用するJdbcTemplate設定。
 * アプリの接続をprimary(デフォルト)としているため、フレームワークが使う接続は
 * secondaryにしている。よってBeanのアクセスにはすべてQualifireを付けて行う。
 * トランザクションは不要なのでTransactionMangerのBean登録はしていない。
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
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

    // 機能はspring.sql.init.*互換
    @Bean
    @FrameworkDataSource
    @ConfigurationProperties("rms.datasource.fw.sql")
    SqlInitializationProperties fwSqlInitializationProperties() {
        return new SqlInitializationProperties();
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(
            @FrameworkDataSource DataSource dataSource,
            @FrameworkDataSource SqlInitializationProperties properties) {

        log.info("Run schema script => " + properties.getSchemaLocations());
        log.info("Run data script => " + properties.getDataLocations());

        return new SqlDataSourceScriptDatabaseInitializer(
                determineDataSource(dataSource, properties.getUsername(), properties.getPassword()),
                properties);
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
}
