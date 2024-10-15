package io.extact.msa.spring.platform.fw.auth.jwt;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.UserIdPrincipal;

public class RmsJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String AUTHORITY_PREFIX = "ROLE_";

    private Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;
    private String principalClaimName;

    RmsJwtAuthConverter(Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter,
            String principalClaimName) {
        this.authoritiesConverter = authoritiesConverter;
        this.principalClaimName = principalClaimName;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = this.authoritiesConverter.convert(jwt);
        String principalName = jwt.getClaimAsString(this.principalClaimName);

        // Authorityからgroup名を取得するが余計なROLE_は削除しておく
        Set<String> groups = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleName -> StringUtils.delete(roleName, AUTHORITY_PREFIX))
                .collect(Collectors.toSet());

        LoginUser loginUser = LoginUser.of(Integer.parseInt(jwt.getSubject()), groups);

        return new RmsJwtAuthToken(new UserIdPrincipal(loginUser.getUserId()), principalName, jwt,
                authorities, loginUser);
    }

    public static RmsJwtAuthenticationConverterBuilder builder() {
        return new RmsJwtAuthenticationConverterBuilder();
    }


    public static class RmsJwtAuthenticationConverterBuilder {

        private String authoritiesClaimName;
        private String authorityPrefix;
        private String principalClaimName;
        private JwtGrantedAuthoritiesConverter authoritiesConverter;

        RmsJwtAuthenticationConverterBuilder() {
            defaultSetting();
        }

        private void defaultSetting() {
            this.authoritiesClaimName = "groups";
            this.authorityPrefix = AUTHORITY_PREFIX;
            this.principalClaimName = JwtClaimNames.SUB;
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

        public RmsJwtAuthenticationConverterBuilder principalClaimName(String claimName) {
            this.principalClaimName = claimName;
            return this;
        }

        public RmsJwtAuthenticationConverterBuilder authoritiesConverter(JwtGrantedAuthoritiesConverter converter) {
            this.authoritiesConverter = converter;
            return this;
        }

        public RmsJwtAuthConverter build() {
            authoritiesConverter.setAuthoritiesClaimName(authoritiesClaimName);
            authoritiesConverter.setAuthorityPrefix(authorityPrefix);
            return new RmsJwtAuthConverter(authoritiesConverter, principalClaimName);
        }
    }
}
