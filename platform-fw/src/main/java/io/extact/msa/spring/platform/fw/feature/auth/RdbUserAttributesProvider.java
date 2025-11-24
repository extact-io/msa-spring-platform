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
                , full_name
                , tel
            from
                user_attributes
            where
                user_id = ?
            """;

    @Override
    public RmsUserAttribute provide(AuthUserId id) {
        return template.query(SELECT_SQL, rowMapper(), id.value()).getFirst();
    }

    private RowMapper<RmsUserAttribute> rowMapper() {
        return (rs, _) -> new RmsUserAttribute(
                new AuthUserId(rs.getString("user_id")),
                rs.getString("full_name"),
                rs.getString("tel"));
    }
}
