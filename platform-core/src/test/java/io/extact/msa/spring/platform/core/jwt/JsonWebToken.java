package io.extact.msa.spring.platform.core.jwt;

import java.util.Set;

public interface JsonWebToken {

    String getName();

    Set<String> getGroups();

    Set<String> getClaimNames();

    <T> T getClaim(String claimName, Class<T> type);

    <T> T getClaim(String claimName);
}
