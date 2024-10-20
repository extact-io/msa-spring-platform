package io.extact.msa.spring.platform.fw.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpringDataJpaExecutor<E> extends JpaRepository<E, Integer>, EntityContext<E, Integer> {

    List<E> findAllByOrderByIdAsc();

    default boolean isManaged(E entity) {
        return entityManager().contains(entity);
    }
}
