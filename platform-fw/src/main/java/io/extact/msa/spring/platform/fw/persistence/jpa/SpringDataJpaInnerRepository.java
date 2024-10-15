package io.extact.msa.spring.platform.fw.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpringDataJpaInnerRepository<T> extends JpaRepository<T, Integer>, EntityManagerHolder {

    List<T> findAllByOrderByIdAsc();

    default boolean isManaged(T entity) {
        return entityManager().contains(entity);
    }
}
