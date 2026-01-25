package io.extact.msa.spring.platform.fw.feature.auth;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributesProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RdbAttributesProvider implements LoginUserAttributesProvider<RmsLoginUserAttributes> {

    private final JdbcTemplate template;

    private static final String SELECT_SQL = """
            select user_id, attr1, attr2, attr3 from login_user_attributes where user_id = ?
            """;
    private static final String INSERT_SQL = """
            insert into login_user_attributes (user_id, attr1, attr2, attr3) values (?, ?, ?, ?)
            """;

    @Override
    @Cacheable(cacheNames = LoginUserAttributesCacheKeys.CACHE_NAME)
    public RmsLoginUserAttributes provide(AuthUserId id) {
        log.trace("no cache, so get data...-> id={}", id.value());
        return template.query(SELECT_SQL, rowMapper(), id.value())
                .stream()
                .findAny()
                .orElse(null);
    }

    public void register(RmsLoginUserAttributes attributes) {
        log.trace("insert attributes...-> id={}", attributes.authUserId().value());
        template.update(
                INSERT_SQL,
                attributes.authUserId().value(),
                attributes.attr1(),
                attributes.attr2(),
                attributes.attr3());
    }

    private RowMapper<RmsLoginUserAttributes> rowMapper() {
        return (rs, _) -> new RmsLoginUserAttributes(
                new AuthUserId(rs.getString("user_id")),
                rs.getString("attr1"),
                rs.getString("attr2"),
                rs.getString("attr3"));
    }
}
