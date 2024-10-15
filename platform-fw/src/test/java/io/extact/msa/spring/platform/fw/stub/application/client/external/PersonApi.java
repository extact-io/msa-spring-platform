package io.extact.msa.spring.platform.fw.stub.application.client.external;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.AddPersonClientRequest;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.PersonClientResponse;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.UpdatePersonClientRequest;

@HttpExchange("/persons")
public interface PersonApi {

    @GetExchange
    List<PersonClientResponse> getAll();

    @GetExchange("/{id}")
    PersonClientResponse get(@PathVariable("id") Integer id);

    @PostExchange
    PersonClientResponse add(@RequestBody AddPersonClientRequest req);

    @PutExchange
    PersonClientResponse update(@RequestBody UpdatePersonClientRequest req);

    @DeleteExchange("/{id}")
    void delete(@PathVariable("id") Integer itemId);
}
