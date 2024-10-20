package io.extact.msa.spring.platform.fw.persistence;

import static io.extact.msa.spring.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.method.MethodValidationException;

import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;

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

        Person expected = Person.valueOf(1, "name1");
        Optional<Person> actual = repository().find(1);

        assertThat(actual).isPresent();
        assertThatToString(actual.get()).isEqualTo(expected);

        actual = repository().find(999);
        assertThat(actual).isNotPresent();
    }

    @Test
    void testGetAll() {
        List<Person> actual = repository().findAll();
        assertThat(actual).hasSize(4);
    }

    @Test
    void testUpdate() {
        Person expected = Person.valueOf(4, "UP");
        Optional<Person> actual = repository().update(Person.valueOf(4, "UP"));
        assertThatToString(actual.get()).isEqualTo(expected);
    }

    @Test
    void testUpdateOnValidationError() {
        Throwable thrown = catchThrowable(() -> repository().update(Person.valueOf(4, "123456"))); // 5文字より大きい
        thrown.printStackTrace();
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }

    @Test
    void testUpdateOnDuplicate() {
        // 重複チェックは上位で行うのでノーチェックであることを確認
        Optional<Person> actual = repository().update(Person.valueOf(2, "name3"));
        assertThat(actual).isPresent();
    }

    @Test
    void testUpdateOnNotFound() {
        Optional<Person> actual = repository().update(Person.valueOf(999, "UP"));
        assertThat(actual).isNotPresent();
    }

    protected abstract void testAddToSpecificImplementation();

    @Test
    void testAddOnValidationError() {
        Throwable thrown = catchThrowable(() -> repository().add(Person.valueOf(null, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }

    @Test
    void testAddOnDuplicateError() {
        // 重複チェックは上位で行うので正常に処理できることを確認
        assertThatCode(() -> repository().add(Person.valueOf(null, "name3")))
                .doesNotThrowAnyException();
    }

    protected abstract void testDeleteToSpecificImplementation();

    @Test
    void testDeleteOnValidationError() {
        Throwable thrown = catchThrowable(() -> repository().delete(Person.valueOf(null, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }

    protected abstract void testDeleteOnNotFoundToSpecificImplementation();
}
