package io.extact.msa.spring.platform.fw.stub.application.server.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.extact.msa.spring.platform.fw.controller.RmsRestController;
import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.application.server.application.EditPersonCommand;
import io.extact.msa.spring.platform.fw.stub.application.server.application.PersonApplicationService;
import io.extact.msa.spring.platform.fw.stub.application.server.application.RegisterPersonCommand;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonId;
import lombok.RequiredArgsConstructor;

@RmsRestController("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonApplicationService service;

    @GetMapping
    public List<PersonResponse> getAll() {
        return service.getAll().stream()
                .map(PersonResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PersonResponse get(@RmsId @PathVariable("id") Integer personId) {
        return service.getById(new PersonId(personId))
                .map(PersonResponse::from)
                .orElse(null);
    }

    @PostMapping
    public PersonResponse add(@Valid @RequestBody AddPersonRequest request) {
        RegisterPersonCommand command = request.transform(this::toRegisterCommand);
        return service.register(command)
                .transform(PersonResponse::from);
    }

    @PutMapping
    public PersonResponse update(@Valid @RequestBody UpdatePersonRequest request) {
        EditPersonCommand command = request.transform(this::toEditCommand);
        return service.edit(command)
                .transform(PersonResponse::from);
    }

    @DeleteMapping("/{id}")
    public void delete(@RmsId @PathVariable("id") Integer personId) {
        service.delete(new PersonId(personId));
    }


    private RegisterPersonCommand toRegisterCommand(AddPersonRequest req) {
        return new RegisterPersonCommand(req.name());
    }

    private EditPersonCommand toEditCommand(UpdatePersonRequest req) {
        return new EditPersonCommand(req.personId(), req.name());
    }
}
