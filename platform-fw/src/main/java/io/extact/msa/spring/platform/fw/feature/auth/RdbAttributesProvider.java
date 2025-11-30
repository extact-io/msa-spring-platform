package io.extact.msa.spring.platform.fw.feature.auth;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributesProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RdbAttributesProvider implements LoginUserAttributesProvider<RmsLoginUserAttributes> {

    private final JdbcTemplate template;

    private static final String SELECT_SQL = """
            select user_id, attr1, attr2, attr3 from login_user_attributes where user_id = ?
            """;

    @Override
    @Cacheable(cacheNames = "${rms.login-user-attributes.cache-name}")
    public RmsLoginUserAttributes provide(AuthUserId id) {
        return template.query(SELECT_SQL, rowMapper(), id.value())
                .stream()
                .findAny()
                .orElse(null);
    }

    private RowMapper<RmsLoginUserAttributes> rowMapper() {
        return (rs, _) -> new RmsLoginUserAttributes(
                new AuthUserId(rs.getString("user_id")),
                rs.getString("attr1"),
                rs.getString("attr2"),
                rs.getString("attr3"));
    }
}
