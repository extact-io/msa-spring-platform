package io.extact.msa.spring.platform.fw.stub.application.server.controller;

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
import io.extact.msa.spring.platform.fw.stub.application.server.controller.dto.AddPersonRequest;
import io.extact.msa.spring.platform.fw.stub.application.server.controller.dto.PersonResponse;
import io.extact.msa.spring.platform.fw.stub.application.server.controller.dto.UpdatePersonRequest;
import io.extact.msa.spring.platform.fw.stub.application.server.service.PersonService;
import lombok.RequiredArgsConstructor;

@RmsRestController("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

    @GetMapping
    public List<PersonResponse> getAll() {
        return service.findAll().stream()
                .map(PersonResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PersonResponse get(@RmsId @PathVariable("id") Integer itemId) {
        return service.get(itemId)
                .map(PersonResponse::from)
                .orElse(null);
    }

    @PostMapping
    public PersonResponse add(@Valid @RequestBody AddPersonRequest request) {
        return service.add(request.toEntity())
                .transform(PersonResponse::from);
    }

    @PutMapping
    public PersonResponse update(@Valid @RequestBody UpdatePersonRequest request) {
        return service.update(request.toEntity())
                .map(PersonResponse::from)
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@RmsId @PathVariable("id") Integer itemId) {
        service.delete(itemId);
    }
}
