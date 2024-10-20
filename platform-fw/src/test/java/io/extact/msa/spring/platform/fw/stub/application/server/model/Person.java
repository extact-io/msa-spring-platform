package io.extact.msa.spring.platform.fw.stub.application.server.model;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id")
public class Person implements DomainModel {

    private @NonNull PersonId id;
    private @NonNull @NonFinal String name;

    public static Person reconstruct(PersonId id, String name) {
        return new Person(id, name);
    }

    public void changeName(String name) {
        this.name = name;
    }
}
