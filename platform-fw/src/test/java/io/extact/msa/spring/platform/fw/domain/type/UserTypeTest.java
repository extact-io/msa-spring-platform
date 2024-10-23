package io.extact.msa.spring.platform.fw.domain.type;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.extact.msa.spring.platform.fw.domain.type.UserType;

class UserTypeTest {

    @Test
    void testIsAmdin() {
        assertThat(UserType.ADMIN.isAdmin()).isTrue();
        assertThat(UserType.MEMBER.isAdmin()).isFalse();
    }

    @Test
    void testIsValidUserType() {
        assertThat(UserType.isValidUserType("ADMIN")).isTrue();
        assertThat(UserType.isValidUserType("MEMBER")).isTrue();
        assertThat(UserType.isValidUserType("admin")).isFalse();
        assertThat(UserType.isValidUserType("member")).isFalse();
    }
}
