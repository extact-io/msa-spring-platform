package io.extact.msa.spring.platform.fw.stub.apps.person.domain.model;

import java.util.Objects;

import io.extact.msa.spring.platform.fw.domain.model.EntityModelView;

public interface PersonModelView extends EntityModelView<PersonModelView> {

    PersonId getId();

    String getName();

    @Override
    default boolean isEqual(PersonModelView other) {
        if (other == null) {
            return false;
        }
        return Objects.equals(getId(), other.getId())
                && Objects.equals(getName(), other.getName());
    }
}
