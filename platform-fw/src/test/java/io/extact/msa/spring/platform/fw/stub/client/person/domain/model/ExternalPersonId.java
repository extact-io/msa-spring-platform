package io.extact.msa.spring.platform.fw.stub.client.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.model.Identity;

public record ExternalPersonId(
        @RmsId int id) implements Identity {

    public static final ExternalPersonId TRANSIENT_ID = new ExternalPersonId(-1);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalPersonId that)) {
            return false;
        }
        if (this == TRANSIENT_ID) {
            return false;
        }
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        if (this == TRANSIENT_ID) {
            return System.identityHashCode(this);
        }
        return Integer.hashCode(id);
    }
}
