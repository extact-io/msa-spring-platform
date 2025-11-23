package io.extact.msa.spring.platform.fw.feature.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.UserAttributesProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RdbUserAttributesProvider implements UserAttributesProvider<RmsUserAttribute> {

    private final JdbcTemplate template;

    private static final String SELECT_SQL = """
            select
                  user_id
                , login_id
                , user_name
                , phone_number
            from
                user_attribute
            where
                user_id = ?
            """;

    @Override
    public RmsUserAttribute provide(AuthUserId id) {
        return template.query(SELECT_SQL, rowMapper(), id.value()).getFirst();
    }

    private RowMapper<RmsUserAttribute> rowMapper() {
        return (rs, _) -> new RmsUserAttribute(
                new AuthUserId(rs.getString("auth_user_id")),
                rs.getString("login_id"),
                rs.getString("user_name"),
                rs.getString("phone_number"));
    }
}
