package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.remote.GenericClientApi;

@HttpExchange("/remote-persons")
public interface RemotePersonClientApi extends GenericClientApi<RemotePerson> {

    @GetExchange("/{id}")
    @Override
    RemotePerson get(@PathVariable Integer id);

    @GetExchange
    @Override
    List<RemotePerson> getAll();

    @PostExchange
    @Override
    void add(@RequestBody RemotePerson item);

    @PutExchange
    @Override
    boolean update(@RequestBody RemotePerson item);

    @DeleteExchange("/{id}")
    @Override
    boolean delete(@PathVariable Integer id);

    @GetExchange("/next-identity")
    @Override
    int nextIdentity();

    @GetExchange("/unique")
    RemotePerson findByName(@RequestParam String name);
}
