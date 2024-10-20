package io.extact.msa.spring.platform.fw.domain;

public interface Identifiable {

    Identity getId();

    default boolean isEqualTo(Identifiable other) {
        if (other == null) {
            return false;
        }
        if (this.getId() == null) {
            return false;
        }
        return this.getId().equals(other.getId());
    }
}
