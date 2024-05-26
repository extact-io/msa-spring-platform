package io.extact.msa.spring.platform.core.jwt.provider;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import io.extact.msa.spring.platform.core.jwt.provider.config.ConditionalOnEnabledJwtProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * ログイン情報をもとに生成したJWTをBearTokenとしてHTTPヘッダに設定する。
 * ターゲットメソッドで例外が発生した場合このAdviceは呼び出されることはない。
 */
@RestControllerAdvice
@ConditionalOnEnabledJwtProvider
@Slf4j
public class JwtProvideResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final String BEARER_MARK = "Bearer";

    private JsonWebTokenGenerator tokenGenerator;

    public JwtProvideResponseAdvice(JsonWebTokenGenerator generator) {
        this.tokenGenerator = generator;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethodAnnotation(GenerateToken.class) != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {

        if (body == null) {
            log.warn("Reponse body is not set.");
            return body;
        }

        if (!(body instanceof UserClaims)) {
            log.warn("The instance of the body isn't UserClaims. [class={}]", body.getClass().getName());
            return body;
        }

        // JwtTokenの生成
        String jwtToken = tokenGenerator.generateToken((UserClaims) body);
        log.info("Generated JWT-Token=>[{}]", jwtToken); // ホントはログに書いちゃダメだけどネ

        HttpHeaders headers = response.getHeaders();
        headers.add("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
        headers.add(HttpHeaders.AUTHORIZATION, BEARER_MARK + " " + jwtToken);

        return body;
    }
}
