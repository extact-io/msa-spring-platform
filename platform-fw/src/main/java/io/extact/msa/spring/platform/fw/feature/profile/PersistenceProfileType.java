package io.extact.msa.spring.platform.fw.feature.profile;

import java.util.Optional;
import java.util.stream.Stream;

public enum PersistenceProfileType {

    JPA("-jpa"),
    FILE("-file");

    private final String profileSuffix;

    public static Optional<PersistenceProfileType> from(String profileName) {
        return Stream.of(values())
                .filter(type -> type.matchesType(profileName))
                .findFirst();
    }

    public Optional<String> resolveEntityName(String profileName) {
        if (this.matchesType(profileName)) {

            String entityName = removeSuffix(profileName, this.profileSuffix);
            return Optional.of(entityName);
        }
        return Optional.empty();
    }

    PersistenceProfileType(String profileSuffix) {
        this.profileSuffix = profileSuffix;
    }

    boolean matchesType(String profileName) {
        return profileName.endsWith(this.profileSuffix);
    }

    String removeSuffix(String input, String suffix) {
        if (input != null && suffix != null && input.endsWith(suffix)) {
            return input.substring(0, input.length() - suffix.length());
        }
        return input;
    }
}
