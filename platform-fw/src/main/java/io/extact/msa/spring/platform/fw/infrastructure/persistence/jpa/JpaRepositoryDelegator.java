package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaRepositoryDelegator<E> extends JpaRepository<E, Integer> {

    List<E> findAllByOrderByIdAsc();
}
