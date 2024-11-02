package io.extact.msa.spring.platform.core.jwt.encode;

import java.util.Set;

/**
 * JWTの元ネタを表すインタフェース。
 * <code>@GenerateToken</code>を付けてRESTリソースのメソッドの戻り値には
 * このインタフェースを実装すること。
 * @see GenerateToken
 */
public interface UserClaims {

    String userId();

    String principalName();

    Set<String> groups();
}
