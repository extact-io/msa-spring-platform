package io.extact.msa.spring.platform.core.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring Security のフィルタチェーン内で発生した想定外の例外を捕捉し、
 * アプリケーション共通の例外処理（HandlerExceptionResolver）へ委譲す
 * るための Filter。
 * <p>
 * 認証失敗（AuthenticationException）と認可失敗（AccessDeniedException）は
 * Spring Security 標準の例外処理（401 / 403）に委ねるため、この Filter
 * ではハンドリングしない。
 */
public class SecurityFallbackExceptionFilter extends OncePerRequestFilter {

    // TODO: ちゃんとしたところから持ってくる
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (response.isCommitted()) {
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (AuthenticationException | AccessDeniedException ex) {
            // 原則 AuthenticationExceptionは自分の認証Filterで、AccessDeniedExceptionはExceptionTranslationFilterで
            // 捕捉されるため、このFilterにはこないが、念のため別処理にする
            // TODO: EntryPointとHanlderを持ってくる
            throw ex; // 401/403 は 後続の ExceptionTranslationFilter に任せる
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            writeJsonError(response, HttpStatus.INTERNAL_SERVER_ERROR, "system_error", ex.getMessage());
        }
    }

    private void writeJsonError(HttpServletResponse response, HttpStatus status, String code, String message)
            throws IOException {

        if (response.isCommitted())
            return;

        SecurityContextHolder.clearContext();

        response.resetBuffer();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), Map.of(
                "error", code,
                "message", message));

        response.flushBuffer();
    }
}
