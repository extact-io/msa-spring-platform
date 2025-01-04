package io.extact.msa.spring.platform.fw.infrastructure.framework.sqlinit;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.infrastructure.framework.profile.ActiveProfile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ProfileBasedDbInitializerSettingsCreator {

    static final String SCHEMA_LOCATION_KEY_TEMPLATE = "rms.persistence.%s.sql.schema-locations";
    static final String DATA_LOCATION_KEY_TEMPLATE = "rms.persistence.%s.sql.data-locations";

    static DatabaseInitializationSettings create(Environment env, SqlInitializationProperties properties) {

        List<String> entities = resoleveJpaEntitiesFrom(env.getActiveProfiles());
        List<String> schemaLocations = resoleveSchemaLocationsFor(entities, env);
        List<String> dataLocations = resoleveDataLocationsFor(entities, env);

        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();

        settings.setSchemaLocations(schemaLocations);
        settings.setDataLocations(dataLocations);
        settings.setContinueOnError(properties.isContinueOnError());
        settings.setSeparator(properties.getSeparator());
        settings.setEncoding(properties.getEncoding());
        settings.setMode(properties.getMode());

        return settings;
    }

    private static List<String> resoleveJpaEntitiesFrom(String[] activeProfiles) {
        return Stream.of(activeProfiles)
                .map(ActiveProfile::new)
                .filter(ActiveProfile::isJpaProfile)
                .map(ActiveProfile::getTargetEntity)
                .toList();
    }

    private static List<String> resoleveSchemaLocationsFor(List<String> entityNames, Environment env) {
        return entityNames
                .stream()
                .map(SCHEMA_LOCATION_KEY_TEMPLATE::formatted)
                .map(env::getRequiredProperty)
                .toList();
    }

    private static List<String> resoleveDataLocationsFor(List<String> entityNames, Environment env) {
        return entityNames
                .stream()
                .map(DATA_LOCATION_KEY_TEMPLATE::formatted)
                .map(env::getRequiredProperty)
                .toList();
    }
}
