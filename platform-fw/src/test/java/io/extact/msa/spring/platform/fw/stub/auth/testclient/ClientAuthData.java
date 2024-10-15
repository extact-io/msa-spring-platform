package io.extact.msa.spring.platform.fw.stub.auth.testclient;

import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientAuthData {

    private String userId;
    private Set<String> groups;

    public String getUserPrincipalName() {
        return userId + "@msa-rms";
    }
}
