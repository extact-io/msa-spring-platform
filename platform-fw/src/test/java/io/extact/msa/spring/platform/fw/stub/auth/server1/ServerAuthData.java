package io.extact.msa.spring.platform.fw.stub.auth.server1;

import java.util.Set;

import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ServerAuthData implements UserClaims {

    private String userId;
    private Set<String> groups;

    @Override
    public String getUserPrincipalName() {
        return userId + "@msa-rms";
    }
}
