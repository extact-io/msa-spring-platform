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

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.UserIdPrincipal;

public class RmsJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;
    private String principalClaimName;

    RmsJwtAuthenticationConverter(Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter,
            String principalClaimName) {
        this.authoritiesConverter = authoritiesConverter;
        this.principalClaimName = principalClaimName;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = this.authoritiesConverter.convert(jwt);
        String principalName = jwt.getClaimAsString(this.principalClaimName);

        Set<String> groups = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        LoginUser loginUser = LoginUser.of(Integer.parseInt(jwt.getSubject()), groups);

        return new RmsJwtAuthenticationToken(new UserIdPrincipal(loginUser.getUserId()), principalName, jwt,
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
            this.authorityPrefix = "ROLE_";
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

        public RmsJwtAuthenticationConverter build() {
            authoritiesConverter.setAuthoritiesClaimName(authoritiesClaimName);
            authoritiesConverter.setAuthorityPrefix(authorityPrefix);
            return new RmsJwtAuthenticationConverter(authoritiesConverter, principalClaimName);
        }
    }
}
