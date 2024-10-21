package io.extact.msa.spring.platform.fw.persistence.jpa;


import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import io.extact.msa.spring.platform.fw.domain.Identity;
import io.extact.msa.spring.platform.fw.exception.RmsPersistenceException;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;

public abstract class AbstractJpaRepository<M extends DomainModel, E extends TableEntity<M>>
        implements GenericRepository<M> {

    @Autowired
    private EntityManager entityManager;

    private final ModelEntityMapper<M, E> modelEntityMapper;
    private final SpringDataJpaExecutor<E> executor;
    private final EntityManagerHolder entityManagerHolder;
    private final SequenceGenerator<E> sequenceGenerator;

    private JpaMetamodelEntityInformation<E, Integer> entityInformation;

    public AbstractJpaRepository(SpringDataJpaExecutor<E> executor,
            ModelEntityMapper<M, E> modelEntityMapper, EntityManagerHolder entityManagerHolder) {
        this.modelEntityMapper = modelEntityMapper;
        this.executor = executor;
        this.sequenceGenerator = new SequenceGenerator<E>();
        this.entityManagerHolder = entityManagerHolder;
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
    public void update(M model) {
        E entity = model.transform(modelEntityMapper::toEntity);
        if (!entityManagerHolder.entityManager().contains(entity)
                && executor.findById(model.getId().id()).isEmpty()) {
            new RmsPersistenceException("target does not exist for pk:" + entity.getPk());
        }
        executor.saveAndFlush(entity);
    }

    @Override
    public void delete(M model) {
        E entity = model.transform(modelEntityMapper::toEntity);
        executor.delete(entity);
        entityManagerHolder.entityManager().flush();
    }

    @Override
    public int nextIdentity() {
        return (int) sequenceGenerator.generate();
    }

    class SequenceGenerator<E> {

        long generate() {
            String template = "SELECT NEXT VALUE FOR %s;"; // for H2
            String seqName = resolveSequenceName();
            Query query = entityManagerHolder.entityManager().createNativeQuery(template.formatted(seqName));
            return (Long) query.getSingleResult();
        }

        String resolveSequenceName() {

            ResolvableType resolvableType = ResolvableType.forClass(AbstractJpaRepository.this.getClass());
            ResolvableType generic = resolvableType.as(AbstractJpaRepository.class).getGeneric(0);

            String entityClassName = generic.resolve().getSimpleName().toLowerCase();

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
