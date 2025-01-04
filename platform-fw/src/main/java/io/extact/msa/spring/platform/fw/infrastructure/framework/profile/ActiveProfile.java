package io.extact.msa.spring.platform.fw.infrastructure.framework.profile;

import java.util.Optional;

import lombok.Getter;

public class ActiveProfile {

    private final @Getter String profileName;
    private final Optional<PersistenceProfileType> persistenceType;

    public ActiveProfile(String profileName) {
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
