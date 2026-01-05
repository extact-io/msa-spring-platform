package io.extact.msa.spring.platform.fw.infrastructure.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties.Xa;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;

/**
 * {@link DataSourceProperties}を実体として持つデータクラス。
 * <code>spring.datasource.*</code>の値をDataSourcePropertiesと継承関係のない
 * このクラスに一旦バインドさせることでDataSourcePropertiesへのインジェクション
 * 対象にならないようにしている。
 * <p>
 * このため{@link DataSourceProperties}を同じフィールドを持つようにしている。
 */
class DataSourcePropertiesHolder implements BeanClassLoaderAware, InitializingBean {

    private DataSourceProperties delegate;

    DataSourcePropertiesHolder() {
        delegate = new DataSourceProperties();
    }

    DataSourceProperties unwrap() {
        return delegate;
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        delegate.setBeanClassLoader(classLoader);
    }

    public void afterPropertiesSet() throws Exception {
        delegate.afterPropertiesSet();
    }

    public DataSourceBuilder<?> initializeDataSourceBuilder() {
        return delegate.initializeDataSourceBuilder();
    }

    public boolean isGenerateUniqueName() {
        return delegate.isGenerateUniqueName();
    }

    public void setGenerateUniqueName(boolean generateUniqueName) {
        delegate.setGenerateUniqueName(generateUniqueName);
    }

    public String getName() {
        return delegate.getName();
    }

    public void setName(String name) {
        delegate.setName(name);
    }

    public Class<? extends DataSource> getType() {
        return delegate.getType();
    }

    public void setType(Class<? extends DataSource> type) {
        delegate.setType(type);
    }

    public String getDriverClassName() {
        return delegate.getDriverClassName();
    }

    public void setDriverClassName(String driverClassName) {
        delegate.setDriverClassName(driverClassName);
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public String determineDriverClassName() {
        return delegate.determineDriverClassName();
    }

    public String getUrl() {
        return delegate.getUrl();
    }

    public void setUrl(String url) {
        delegate.setUrl(url);
    }

    public String determineUrl() {
        return delegate.determineUrl();
    }

    public String determineDatabaseName() {
        return delegate.determineDatabaseName();
    }

    public String getUsername() {
        return delegate.getUsername();
    }

    public void setUsername(String username) {
        delegate.setUsername(username);
    }

    public String determineUsername() {
        return delegate.determineUsername();
    }

    public String getPassword() {
        return delegate.getPassword();
    }

    public void setPassword(String password) {
        delegate.setPassword(password);
    }

    public String determinePassword() {
        return delegate.determinePassword();
    }

    public String getJndiName() {
        return delegate.getJndiName();
    }

    public void setJndiName(String jndiName) {
        delegate.setJndiName(jndiName);
    }

    public EmbeddedDatabaseConnection getEmbeddedDatabaseConnection() {
        return delegate.getEmbeddedDatabaseConnection();
    }

    public void setEmbeddedDatabaseConnection(EmbeddedDatabaseConnection embeddedDatabaseConnection) {
        delegate.setEmbeddedDatabaseConnection(embeddedDatabaseConnection);
    }

    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

    public Xa getXa() {
        return delegate.getXa();
    }

    public void setXa(Xa xa) {
        delegate.setXa(xa);
    }

    public String toString() {
        return delegate.toString();
    }
}