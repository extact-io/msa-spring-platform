package io.extact.msa.spring.platform.core.jwt.provider;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 認証トークンの発行を表すアノテーション。
 * このアノテーションが付与されているメソッド実行後に認証トークンの発行が行われる。
 * このアノテーションが付与されたメソッドの戻り値からJWTを生成する。JWTを生成するのに
 * 必要な情報を取得できるようにアノテーションを付与したメソッドの戻り値は{@link UserClaims}を
 * 実装すること。
 */
@Inherited
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface GenerateToken {
}
