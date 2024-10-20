package io.extact.msa.spring.platform.fw.persistence.jpa;


import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.springframework.core.ResolvableType;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import io.extact.msa.spring.platform.fw.domain.Identity;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;
import lombok.RequiredArgsConstructor;

public abstract class AbstractJpaRepository<M extends DomainModel, E extends TableEntity<M>>
        implements GenericRepository<M> {

    private final ModelEntityMapper<M, E> modelEntityMapper;
    private final SpringDataJpaExecutor<E> executor;
    private final SequenceGenerator<E> sequenceGenerator;


    public AbstractJpaRepository(SpringDataJpaExecutor<E> executor,
            ModelEntityMapper<M, E> modelEntityMapper) {
        this.modelEntityMapper = modelEntityMapper;
        this.executor = executor;
        this.sequenceGenerator = new SequenceGenerator<E>(executor);
    }

    @Override
    public Optional<M> find(Identity id) {
        Optional<E> entity = executor.findById(id.id());
        return entity.map(E::toModel);

    }

    @Override
    public List<M> findAll() {
        return executor.findAllByOrderByIdAsc().stream()
                .map(E::toModel)
                .toList();
    }

    @Override
    public void add(M model) {
        E entity = model.transform(modelEntityMapper::toEntity);
        executor.saveAndFlush(entity);
    }

    @Override
    public Optional<M> update(M model) {
        E entity = model.transform(modelEntityMapper::toEntity);
        if (!executor.entityManager().contains(entity)
                && executor.findById(model.getId().id()).isEmpty()) {
            return Optional.empty();
        }
        E updated = executor.saveAndFlush(entity);
        return Optional.of(updated.toModel());
    }

    @Override
    public void delete(M model) {
        E entity = model.transform(modelEntityMapper::toEntity);
        executor.delete(entity);
        entityManager().flush();
    }

    @Override
    public int nextIdentity() {
        return sequenceGenerator.generate();
    }

    public EntityManager entityManager() {
        return executor.entityManager();
    }

    @RequiredArgsConstructor
    static class SequenceGenerator<E> {

        final SpringDataJpaExecutor<E> executor;

        int generate() {
            String template = "SELECT NEXT VALUE FOR %s;"; // for H2
            String seqName = resolveSequenceName();
            Query query = executor.entityManager().createNativeQuery(template.formatted(seqName));
            return (Integer) query.getSingleResult();
        }

        String resolveSequenceName() {
            String entityClassName = executor.entityClass().getSimpleName().toLowerCase();
            if (entityClassName.endsWith("entity")) {
                return entityClassName.substring(0, entityClassName.length() - "entity".length()) + "_seq";
            } else {
                return entityClassName.toLowerCase() + "_seq";
            }
        }
    }

    static abstract class Foo<Param, Ret> {

        abstract Ret execute(Param parame);

        String type() {
            ResolvableType resolvableType = ResolvableType.forClass(this.getClass());
            ResolvableType generic = resolvableType.as(Foo.class).getGeneric(0);
            return generic.resolve().getSimpleName();
        }
    }

    static class FooImpl extends Foo<Integer, String> {
        @Override
        String execute(Integer parame) {
            return null;
        }
    }

    static abstract class Bar<Param extends List<?>, Ret extends Object> extends Foo<Param, Ret> {
    }

    static class BarImpl extends Bar<List<String>, DomainModel> {
        @Override
        DomainModel execute(List<String> parame) {
            // TODO 自動生成されたメソッド・スタブ
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("FooImpl:" + new FooImpl().type());
        System.out.println("BarImpl:" + new BarImpl().type());
    }
}
