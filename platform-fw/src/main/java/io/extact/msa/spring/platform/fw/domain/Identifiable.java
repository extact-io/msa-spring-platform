package io.extact.msa.spring.platform.fw.domain;

public interface Identifiable {

    Identity getId();

    //void setId(Integer id);

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
