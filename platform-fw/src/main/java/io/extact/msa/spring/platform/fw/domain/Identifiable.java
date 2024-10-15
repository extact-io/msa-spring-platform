package io.extact.msa.spring.platform.fw.domain;

public interface Identifiable {

    Integer getId();

    void setId(Integer id);

    default boolean isSameId(Identifiable other) {
        if (other == null) {
            return false;
        }
        if (this.getId() == null) {
            return false;
        }
        return this.getId().equals(other.getId());
    }
}
