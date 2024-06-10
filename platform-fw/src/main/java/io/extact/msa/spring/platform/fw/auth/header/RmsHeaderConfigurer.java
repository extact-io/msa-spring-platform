package io.extact.msa.spring.platform.fw.auth.header;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;

public class RmsHeaderConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<HttpBasicConfigurer<H>, H> {

    private Optional<SecurityContextHolderStrategy> securityContextHolderStrategy = Optional.empty();
    private Optional<AuthenticationEntryPoint> authenticationEntryPoint = Optional.empty();
    private Optional<AuthenticationFailureHandler> authenticationFailureHandler = Optional.empty();
    private Optional<SecurityContextRepository> securityContextRepository = Optional.empty();

    public RmsHeaderConfigurer<H> SecurityContextHolderStrategy(SecurityContextHolderStrategy strategy) {
        Assert.notNull(strategy, "strategy cannot be null");
        this.securityContextHolderStrategy = Optional.of(strategy);
        return this;
    }

    public RmsHeaderConfigurer<H> authenticationEntryPoint(AuthenticationEntryPoint entryPoint) {
        Assert.notNull(entryPoint, "entryPoint cannot be null");
        this.authenticationEntryPoint = Optional.of(entryPoint);
        return this;
    }

    public RmsHeaderConfigurer<H> authenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.authenticationFailureHandler = Optional.of(failureHandler);
        return this;
    }

    public RmsHeaderConfigurer<H> securityContextRepository(SecurityContextRepository repository) {
        Assert.notNull(repository, "repository cannot be null");
        this.securityContextRepository = Optional.of(repository);
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        http.authenticationProvider(new RmsHeaderAuthenticationProvider());
    }

    @Override
    public void configure(H http) throws Exception {

        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

        RmsHeaderAuthenticationFilter filter = new RmsHeaderAuthenticationFilter(authenticationManager);

        securityContextHolderStrategy.ifPresent(filter::setSecurityContextHolderStrategy);
        authenticationEntryPoint.ifPresent(filter::setAuthenticationEntryPoint);
        authenticationFailureHandler.ifPresent(filter::setAuthenticationFailureHandler);
        securityContextRepository.ifPresent(filter::setSecurityContextRepository);

        http.addFilterAfter(filter, BearerTokenAuthenticationFilter.class);
    }

    public SecurityFilterChain build() throws Exception {
        return this.getBuilder().build();
    }
}
