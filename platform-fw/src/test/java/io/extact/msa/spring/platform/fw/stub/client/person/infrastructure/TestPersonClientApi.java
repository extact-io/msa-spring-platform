package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange("/persons")
public interface TestPersonClientApi {

    @GetExchange
    List<TestPersonResponse> getAll();

    @GetExchange("/{id}")
    TestPersonResponse get(@PathVariable("id") Integer id);

    @PostExchange
    TestPersonResponse add(@RequestBody String name);

    @PutExchange
    TestPersonResponse update(@RequestBody UpdateTestPersonRequest req);

    @DeleteExchange("/{id}")
    void delete(@PathVariable("id") Integer itemId);
}
