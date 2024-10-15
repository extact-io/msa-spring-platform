package io.extact.msa.spring.platform.fw.auth.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class BearerTokenExtractor {

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    public static String extract(HttpHeaders headers) {

        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
            return null;
        }

        Matcher matcher = authorizationPattern.matcher(authorization);
        if (!matcher.matches()) {
            return null;
        }

        return matcher.group("token");
    }
}
