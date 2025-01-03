package io.extact.msa.spring.platform.fw.infrastructure.persistence.sql.init;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class DynamicProfileDataSourceScriptInitializer extends DataSourceScriptDatabaseInitializer implements EnvironmentAware {

    private Environment env;

    public DynamicProfileDataSourceScriptInitializer(DataSource dataSource) {
        this(dataSource, getSettings(properties));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
