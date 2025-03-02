package io.extact.msa.spring.platform.fw.feature.profile;

import java.util.Optional;

import lombok.Getter;

public class PersistenceActiveProfile {

    @Getter
    private final String profileName;
    private final Optional<PersistenceProfileType> persistenceType;

    public PersistenceActiveProfile(String profileName) {
        this.profileName = profileName;
        this.persistenceType = PersistenceProfileType.from(profileName);
    }

    public boolean matchesType(PersistenceProfileType type) {
        return persistenceType
                .map(type::equals)
                .orElse(false);
    }

    public boolean isJpaProfile() {
        return persistenceType
                .map(PersistenceProfileType.JPA::equals)
                .orElse(false);
    }

    public String getTargetEntity() {
        if (persistenceType.isEmpty()) {
            return null;
        }
        return persistenceType
                .get()
                .resolveEntityName(profileName)
                .orElse(null);
    }
}
