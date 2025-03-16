package io.extact.msa.spring.platform.fw.feature.validator;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.feature.validator.SpringModelValidatorAdapter.SerializableSupplier;
import lombok.Getter;

class ValidatorPropertyUtilsTest {

    @Getter
    static class Dto {
        private String val;
        private String abcDB;
        private String xyzNTTPartner;
        private boolean success;

        public String execute() {
            return "dummy";
        }
        public String get() {
            return "dummy";
        }
        public boolean is() {
            return false;
        }
    }

    @Test
    void testExtractPropertyNameOk() {
        // given
        Dto dto = new Dto();
        SerializableSupplier<Object> serializable = dto::getVal;
        // when
        String actual = ValidatorPropertyUtils.extractPropertyName(serializable);
        // then
        assertThat(actual).isEqualTo("val");

        // given
        serializable = dto::getAbcDB;
        // when
        actual = ValidatorPropertyUtils.extractPropertyName(serializable);
        // then
        assertThat(actual).isEqualTo("abcDB");

        // given
        serializable = dto::getXyzNTTPartner;
        // when
        actual = ValidatorPropertyUtils.extractPropertyName(serializable);
        // then
        assertThat(actual).isEqualTo("xyzNTTPartner");

        // given
        serializable = dto::isSuccess;
        // when
        actual = ValidatorPropertyUtils.extractPropertyName(serializable);
        // then
        assertThat(actual).isEqualTo("success");
    }

    @Test
    void testExtractPropertyNameNgNotGetter() {
        // given
        Dto dto = new Dto();
        SerializableSupplier<Object> serializable = dto::execute;
        // when
        RmsSystemException thrown = assertThrows(RmsSystemException.class, () -> {
            ValidatorPropertyUtils.extractPropertyName(serializable);
        });
        // then
        assertThat(thrown).hasMessageContaining("getter");
    }

    @Test
    void testExtractPropertyNameNgNoneNameGetter() {
        // given
        Dto dto = new Dto();
        SerializableSupplier<Object> get = dto::get;
        // when
        RmsSystemException thrown = assertThrows(RmsSystemException.class, () -> {
            ValidatorPropertyUtils.extractPropertyName(get);
        });
        // then
        assertThat(thrown).hasMessageContaining("getter");

        // given
        dto = new Dto();
        SerializableSupplier<Object> is = dto::is;
        // when
        thrown = assertThrows(RmsSystemException.class, () -> {
            ValidatorPropertyUtils.extractPropertyName(is);
        });
        // then
        assertThat(thrown).hasMessageContaining("getter");
    }


    @Test
    void testExtractPropertyNameNgNotLambda() {
        // given
        Serializable serializable = new String("test");
        // when
        RmsSystemException thrown = assertThrows(RmsSystemException.class, () -> {
            ValidatorPropertyUtils.extractPropertyName(serializable);
        });
        // then
        assertThat(thrown).hasMessageContaining("failed");
    }
}
