package io.extact.msa.spring.platform.fw.infrastructure.persistence.remote;

import java.util.List;
import java.util.Optional;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.service.IdentityGenerator;
import io.extact.msa.spring.platform.fw.feature.exception.RmsPersistenceException;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.PhysicalEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRemoteRepository<M extends EntityModel, E extends PhysicalEntity<M>>
        implements GenericRepository<M>, IdentityGenerator {


    private final GenericClientApi<E> clientApi;
    private final ModelEntityMapper<M, E> modelEntityMapper;

    @Override
    public Optional<M> find(Identity id) {
        E entity = clientApi.get(id.id());
        return Optional
                .ofNullable(entity)
                .map(modelEntityMapper::toModel);
    }

    @Override
    public List<M> findAll() {
        return clientApi.getAll()
                .stream()
                .map(modelEntityMapper::toModel)
                .toList();
    }

    @Override
    public void add(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        clientApi.add(entity);
    }

    @Override
    public void update(M model) {
        E entity = model.transform(modelEntityMapper::toEnity);
        boolean result = clientApi.update(entity);
        if (result) {
            throw new RmsPersistenceException("target does not exist for id:" + entity.getPk());
        }
    }

    @Override
    public void delete(M model) {
        boolean result = clientApi.delete(model.getId().id());
        if (result) {
            throw new RmsPersistenceException("target does not exist for id:" + model.getId().id());
        }
    }

    @Override
    public int nextIdentity() {
        return clientApi.nextIdentity();
    }
}
