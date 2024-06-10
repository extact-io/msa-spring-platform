package io.extact.msa.spring.platform.fw.auth.header;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.UserIdPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RmsHeaderAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    private AuthenticationEntryPoint authenticationEntryPoint = new RmsHeaderAuthenticationEntryPoint();

    private AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(
            (request, response, exception) -> this.authenticationEntryPoint.commence(request, response, exception));

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    public RmsHeaderAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authenticationRequest;
        try {
            authenticationRequest = createAuthentication(request);
        } catch (InvalidUserIdHeaderException e) {
            this.logger.trace("Sending to authentication entry point since failed to resolve  rms-userId header", e);
            this.authenticationEntryPoint.commence(request, response, e);
            return;
        }

        if (authenticationRequest == null) {
            this.logger.trace("Did not process request since did not find auth header");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
            SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authenticationResult);
            this.securityContextHolderStrategy.setContext(context);
            this.securityContextRepository.saveContext(context, request, response);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Set SecurityContextHolder to %s".formatted(authenticationResult));
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException failed) {
            this.securityContextHolderStrategy.clearContext();
            this.logger.trace("Failed to process authentication request", failed);
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
        }
    }

    private Authentication createAuthentication(HttpServletRequest req) {

        String userId = req.getHeader("rms-userId");
        if (userId == null) {
            return null;
        }

        UserIdPrincipal principal = new UserIdPrincipal(userId);
        if (principal.userId() == LoginUser.ANONYMOUS_USER.getUserId()) {
            return null; // go to AnonymousAuthenticationFilter
        }

        HeaderCredential credentials = new HeaderCredential(userId, req.getHeader("rms-roles"));

        RmsHeaderAuthenticationToken authenticationToken = new RmsHeaderAuthenticationToken(principal, credentials);
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(req));

        return authenticationToken;
    }

    // ---------------------------------------------------- setter for configure

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy strategy) {
        Assert.notNull(strategy, "strategy cannot be null");
        this.securityContextHolderStrategy = strategy;
    }

    public void setSecurityContextRepository(SecurityContextRepository repository) {
        Assert.notNull(repository, "repository cannot be null");
        this.securityContextRepository = repository;
    }

    public void setAuthenticationEntryPoint(final AuthenticationEntryPoint entryPoint) {
        Assert.notNull(entryPoint, "entryPoint cannot be null");
        this.authenticationEntryPoint = entryPoint;
    }

    public void setAuthenticationFailureHandler(final AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.authenticationFailureHandler = failureHandler;
    }
}
