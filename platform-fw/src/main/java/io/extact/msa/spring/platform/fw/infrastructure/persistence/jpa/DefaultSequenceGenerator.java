package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import io.extact.msa.spring.platform.fw.exception.RmsPersistenceException;

public class DefaultSequenceGenerator implements SequenceGenerator {

    private static final String CONNECTION_EXTRACTOR_PROP_NAME ="rms.persistence.connection-extracor";
    private static final String DEFAULT_CONNECTION_EXTRACTOR_CLASS =
            "io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.hibernate.HibernateConnectionExtractor";

    private final String sequenceName;
    private ConnectionExtractor extractor;
    private String cachedDatabaeName;

    public DefaultSequenceGenerator(Class<?> entityClass) {
        this.sequenceName = resolveSequenceName(entityClass);
    }

    @Override
    public void configure(Environment env) {
        String className = env.getProperty(CONNECTION_EXTRACTOR_PROP_NAME, DEFAULT_CONNECTION_EXTRACTOR_CLASS);
        try {
            Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
            this.extractor = (ConnectionExtractor) BeanUtils.instantiateClass(clazz);
        } catch (ClassNotFoundException | LinkageError e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long generate(EntityManager entityManager) {
        if (extractor == null) {
            throw new IllegalStateException("configure method has not been called yet.");
        }
        String databaseName = fetchDatabaseNameIfNeeded(entityManager);
        String sequenceQuery = makeSequenceQuery(databaseName);
        return (Long) entityManager.createNativeQuery(sequenceQuery).getSingleResult();
    }


    // -------------------------------------------------------- private methods

    private String resolveSequenceName(Class<?> entityClass) {
        String entityClassName = entityClass.getSimpleName().toLowerCase();
        if (entityClassName.endsWith("entity")) {
            return entityClassName.substring(0, entityClassName.length() - "entity".length()) + "_seq";
        } else {
            return entityClassName.toLowerCase() + "_seq";
        }
    }

    private String fetchDatabaseNameIfNeeded(EntityManager entityManager) {
        if (cachedDatabaeName == null) {
            try {
                Connection conn = extractor.extractFrom(entityManager);
                cachedDatabaeName = conn.getMetaData().getDatabaseProductName();
            } catch (SQLException e) {
                throw new RmsPersistenceException(e);
            }
        }
        return this.cachedDatabaeName;
    }

    private String makeSequenceQuery(String databaseName) {
        return switch (databaseName) {
            case "H2" -> {
                String template = "SELECT NEXT VALUE FOR %s;"; // for H2
                yield template.formatted(sequenceName);
            }
            default -> throw new UnsupportedOperationException(databaseName);
        };
    }
}
