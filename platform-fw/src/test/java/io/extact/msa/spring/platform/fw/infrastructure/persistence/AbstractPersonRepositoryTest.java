package io.extact.msa.spring.platform.fw.infrastructure.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.method.MethodValidationException;

import io.extact.msa.spring.platform.fw.exception.RmsPersistenceException;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;

/**
 * PersonリポジトリのFileとJPA実装に共通なテストクラス。
 * スーパークラスで宣言されたテストメソッドでは具象側のテストクラスのトランザクション属性は
 * 無視される仕様となっている。ファイル実装に副作用はないため、このクラスで宣言されたテスト
 * メソッドがロールバックされるように@Transactionalと@Rollbackをつけている。
 *
 * @see https://github.com/spring-projects/spring-framework/issues/12480
 */
@Transactional
@Rollback
public abstract class AbstractPersonRepositoryTest {

    protected abstract PersonRepository repository();

    @Test
    void testGet() {

        Person expected = Person.reconstruct(1, "name1");
        Optional<Person> actual = repository().find(new PersonId(1));

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expected);

        actual = repository().find(new PersonId(999));
        assertThat(actual).isNotPresent();
    }

    @Test
    void testGetAll() {
        List<Person> actual = repository().findAll();
        assertThat(actual).hasSize(4);
    }

    @Test
    void testUpdate() {
        Person expected = Person.reconstruct(4, "UP");
        repository().update(Person.reconstruct(4, "UP"));
        assertThat(repository().find(new PersonId(4)).get()).isEqualTo(expected);
    }

    @Test
    void testUpdateOnValidationError() {
        Throwable thrown = catchThrowable(() -> repository().update(Person.reconstruct(4, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }

    @Test
    void testUpdateOnDuplicate() {
        // 重複チェックは上位で行うので正常に処理できることを確認
        assertThatCode(() -> repository().update(Person.reconstruct(2, "name3")))
                .doesNotThrowAnyException();
    }

    @Test
    void testUpdateOnNotFound() {
        Throwable thrown = catchThrowable(() -> repository().update(Person.reconstruct(999, "UP")));
        assertThat(thrown).isInstanceOf(RmsPersistenceException.class).hasMessageContaining("id:" + 999);
    }

    @Test
    void testAdd() {
        Person expected = Person.reconstruct(5, "ADD");
        repository().add(Person.reconstruct(5, "ADD"));
        assertThat(repository().find(new PersonId(5)).get()).isEqualTo(expected);
    }

    @Test
    void testAddOnValidationError() {
        Throwable thrown = catchThrowable(() -> repository().add(Person.reconstruct(5, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }

    @Test
    void testAddOnDuplicateError() {
        // 重複チェックは上位で行うので正常に処理できることを確認
        assertThatCode(() -> repository().add(Person.reconstruct(5, "name3")))
                .doesNotThrowAnyException();
    }

    @Test
    void testDelete() {
        Person deleted = Person.reconstruct(1, "dummy");
        repository().delete(deleted);
        assertThat(repository().find(new PersonId(1))).isNotPresent();
    }


    @Test
    void testDeleteOnValidationError() {
        Throwable thrown = catchThrowable(() -> repository().delete(Person.reconstruct(1, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }

    @Test
    void testDeleteOnNotFound() {
        Throwable thrown = catchThrowable(() -> repository().delete(Person.reconstruct(999, "dummy")));
        assertThat(thrown).isInstanceOf(RmsPersistenceException.class).hasMessageContaining("id:" + 999);
    }

}
