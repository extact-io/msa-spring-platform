package io.extact.msa.spring.platform.fw.infrastructure.persistence.sql.init;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.env.Environment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DynamicProfileSettingsCreator {

    static DatabaseInitializationSettings createFrom(Environment env) {

        List<String> activeJpaProfileSuffixes = resoleveJpaProfileSuffixes(env);
        List<String> schemaLocations = resoleveSchemaLocationsFor(activeJpaProfileSuffixes);
        List<String> dataLocations = resoleveDataLocationsFor(activeJpaProfileSuffixes);

        SqlInitializationProperties initialValues = new SqlInitializationProperties();

        String platform = null;
        boolean continueOnError = false; //
        String separator = null;
        Charset encoding = null;
        DatabaseInitializationMode mode = null;

        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(schemaLocations);
        settings.setDataLocations(dataLocations);
        settings.setContinueOnError(properties.isContinueOnError());
        settings.setSeparator(properties.getSeparator());
        settings.setEncoding(properties.getEncoding());
        settings.setMode(properties.getMode());

        return settings;
    }

    private static List<String> resoleveJpaProfileSuffixes(Environment env) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    private static List<String> resoleveSchemaLocationsFor(List<String> activeJpaProfileSuffixes) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    private static List<String> resoleveDataLocationsFor(List<String> activeJpaProfileSuffixes) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }
}
