package io.extact.msa.spring.platform.fw.domain.model;

public interface Identity extends Comparable<Identity>, ValueModel {

    int id();

    default int compareTo(Identity other) {
        return Integer.compare(this.id(), other.id());
    }
}
