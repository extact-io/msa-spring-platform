package io.extact.msa.spring.platform.fw.stub.server.person.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.server.person.application.PersonService;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.web.RmsRestController;
import lombok.RequiredArgsConstructor;

@RmsRestController("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

    @GetMapping
    public List<PersonResponse> getAll() {
        return service
                .getAll()
                .stream()
                .map(PersonResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PersonResponse get(@RmsId @PathVariable("id") Integer personId) {
        return service
                .getById(new PersonId(personId))
                .map(PersonResponse::from)
                .orElse(null);
    }

    @PostMapping
    public PersonResponse add(@Valid @RequestBody PersonAddRequest request) {
        return service
                .add(request.toCommand())
                .transform(PersonResponse::from);
    }

    @PutMapping
    public PersonResponse update(@Valid @RequestBody PersonUpdateRequest request) {
        return service
                .update(request.toCommand())
                .transform(PersonResponse::from);
    }

    @DeleteMapping("/{id}")
    public void delete(@RmsId @PathVariable("id") Integer personId) {
        service.delete(new PersonId(personId));
    }
}
