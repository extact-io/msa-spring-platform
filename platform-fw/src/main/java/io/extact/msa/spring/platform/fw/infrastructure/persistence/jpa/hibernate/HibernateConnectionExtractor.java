package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.hibernate;

import java.sql.Connection;

import jakarta.persistence.EntityManager;

import org.hibernate.Session;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.ConnectionExtractor;

public class HibernateConnectionExtractor implements ConnectionExtractor {

    @Override
    public Connection extractFrom(EntityManager entityManager) {
        Session session = entityManager.unwrap(Session.class);
        return session.doReturningWork(conn -> conn);
    }
}
