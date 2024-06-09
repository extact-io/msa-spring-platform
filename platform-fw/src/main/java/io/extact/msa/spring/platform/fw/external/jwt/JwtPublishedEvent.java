package io.extact.msa.spring.platform.fw.external.jwt;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEvent;

/**
 * サーバから発行された認証トークンを受信したことを通知するイベント
 */
public class JwtPublishedEvent extends ApplicationEvent {

    /**
     * コンストラクタ。
     *
     * @param jwtHeader サーバから受信した認証ヘッダ情報
     */
    public JwtPublishedEvent(Pair<String, String> jwtHeader) {
        super(jwtHeader);
    }

    /**
     * サーバから受信した認証ヘッダを取得する。
     *
     * @return valueには受信時の生情報を入れているのでbearerも付いている
     */
    @SuppressWarnings("unchecked")
    public Pair<String, String> getJwtHeader() {
        return (Pair<String, String>) getSource();
    }
}
