package io.extact.msa.spring.platform.fw.stub.client.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupport;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
public class ExternalPerson implements EntityModel {

    private @NonNull ExternalPersonId id;
    private @NonNull @PersonName String name;

    @ToString.Exclude
    private ModelPropertySupport modelSupport;

    ExternalPerson(ExternalPersonId id, String name) {
            this.id = id;
            this.name = name;
    }

    public void editName(String newName) {
        modelSupport.setPropertyWithValidation("name", newName);
    }

    @Override
    public void configureSupport(ModelPropertySupportFactory factory) {
        this.modelSupport = factory.create(ExternalPerson::new, this);
    }

    public interface ExternalPersonCreatable {
        default ExternalPerson newInstance(ExternalPersonId id, String name) {
            return new ExternalPerson(id, name);
        }
    }
}
