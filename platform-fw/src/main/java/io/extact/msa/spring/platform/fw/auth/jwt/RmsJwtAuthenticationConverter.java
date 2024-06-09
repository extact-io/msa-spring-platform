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
import org.springframework.util.Assert;

import io.extact.msa.spring.platform.fw.auth.LoginUser;

public class RmsJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private String principalClaimName = JwtClaimNames.SUB;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = this.authoritiesConverter.convert(jwt);
        String principalClaimValue = jwt.getClaimAsString(this.principalClaimName);

        Set<String> groups = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        LoginUser loginUser = LoginUser.of(Integer.parseInt(jwt.getSubject()), groups);

        return new RmsJwtAuthenticationToken(jwt, authorities, principalClaimValue, loginUser);
    }

    public void setJwtGrantedAuthoritiesConverter(
            Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
        Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null");
        this.authoritiesConverter = jwtGrantedAuthoritiesConverter;
    }

    public void setPrincipalClaimName(String principalClaimName) {
        Assert.hasText(principalClaimName, "principalClaimName cannot be empty");
        this.principalClaimName = principalClaimName;
    }
}
