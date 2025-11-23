package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

/**
 * アプリで利用するDataSourceをprimaryとして登録するコンフィグクラス。
 * JPA接続は1つを前提にしているのでDataSoruceを@primaryを付けてデフォルトにし、
 * JPAの構成をAutoConfigurationでできるようにしている。つまり、JPAのEntityMangaer設定や
 * Transaction設定はAutoConfigurationによって行われる。複数のJPA接続を使う場合は
 * secondary側のJPA構成はマニュアルで行う必要があるので注意。
 * <p>
 * DataSourceが1つだけの場合はAutoConfigurationが効くのはHibernateJpaConfigurationクラスの
 * <code>@ConditionalOnSingleCandidate(DataSource.class)</code>がポイント。
 *
 * @see HibernateJpaAutoConfiguration
 * @see HibernateJpaConfiguration
 */
@Configuration(proxyBeanMethods = false)
public class ApplicationDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("rms.datasource.applicaiton")
    DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("rms.datasource.applicaiton.hikari")
    DataSource primaryDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
