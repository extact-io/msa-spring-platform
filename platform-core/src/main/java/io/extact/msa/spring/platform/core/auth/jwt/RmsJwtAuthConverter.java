package io.extact.msa.spring.platform.core.auth.jwt;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.UserAttributes;
import io.extact.msa.spring.platform.core.auth.user.UserAttributesProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RmsJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String AUTHORITY_PREFIX = "ROLE_";

    private final Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;
    private final UserAttributesProvider<UserAttributes> attributesProvider;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = this.authoritiesConverter.convert(jwt);

        // Authorityからgroup名を取得するが余計なROLE_は削除しておく
        Set<String> groups = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleName -> StringUtils.delete(roleName, AUTHORITY_PREFIX))
                .collect(Collectors.toSet());

        AuthUserId userId = new AuthUserId(jwt.getSubject());
        UserAttributes attributes = attributesProvider.provide(userId);
        LoginUser loginUser = LoginUser.of(userId, groups, attributes);

        return new RmsJwtAuthToken(jwt, loginUser, authorities);
    }

    public static RmsJwtAuthenticationConverterBuilder builder() {
        return new RmsJwtAuthenticationConverterBuilder();
    }


    public static class RmsJwtAuthenticationConverterBuilder {

        private String authoritiesClaimName;
        private String authorityPrefix;
        private JwtGrantedAuthoritiesConverter authoritiesConverter;
        private UserAttributesProvider<UserAttributes> attributesProvider;

        RmsJwtAuthenticationConverterBuilder() {
            defaultSetting();
        }

        private void defaultSetting() {
            this.authoritiesClaimName = "groups";
            this.authorityPrefix = AUTHORITY_PREFIX;
            this.authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        }

        public RmsJwtAuthenticationConverterBuilder authoritiesClaimName(String claimName) {
            this.authoritiesClaimName = claimName;
            return this;
        }

        public RmsJwtAuthenticationConverterBuilder authorityPrefix(String prefix) {
            this.authorityPrefix = prefix;
            return this;
        }

        public RmsJwtAuthenticationConverterBuilder authoritiesConverter(JwtGrantedAuthoritiesConverter converter) {
            this.authoritiesConverter = converter;
            return this;
        }

        public RmsJwtAuthenticationConverterBuilder userAttributesProvider(UserAttributesProvider<UserAttributes> provider) {
            this.attributesProvider = provider;
            return this;
        }

        public RmsJwtAuthConverter build() {
            authoritiesConverter.setAuthoritiesClaimName(authoritiesClaimName);
            authoritiesConverter.setAuthorityPrefix(authorityPrefix);
            return new RmsJwtAuthConverter(authoritiesConverter, attributesProvider);
        }
    }
}
