package io.extact.msa.spring.platform.fw.feature.sqlinit;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileBasedDbInitializer extends SqlDataSourceScriptDatabaseInitializer {

    public ProfileBasedDbInitializer(DataSource dataSource, DatabaseInitializationSettings settings) {
        super(dataSource, settings);
        log.info("Run schema script => " + settings.getSchemaLocations());
        log.info("Run data script => " + settings.getDataLocations());
    }
}
