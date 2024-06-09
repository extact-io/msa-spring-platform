package io.extact.msa.spring.platform.fw.external;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;

import io.extact.msa.spring.platform.fw.external.jwt.JwtPublishedEvent;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

/**
 * サーバから発行されたJsonWebTokenをリクエストヘッダに付加するクラス
 */
@Slf4j
public class PropagateJwtRequestInitializer implements ClientHttpRequestInitializer {

    private Pair<String, String> jwtHeader;

    @Override
    public void initialize(ClientHttpRequest request) {
        request.getHeaders().add(jwtHeader.getKey(), jwtHeader.getValue());
    }

    void onEvent(@Observes JwtPublishedEvent event) {
        log.info("ヘッダに追加するJWTを受信しました");
        this.jwtHeader = event.getJwtHeader();
    }
}
