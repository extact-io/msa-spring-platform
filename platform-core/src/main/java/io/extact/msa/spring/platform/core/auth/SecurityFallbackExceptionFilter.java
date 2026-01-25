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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Security のフィルタチェーン内で発生した想定外の例外を捕捉し、
 * エラーレスポンスを返すフィルター。
 */
@RequiredArgsConstructor
@Slf4j
public class SecurityFallbackExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (response.isCommitted()) {
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("exception occurred while processing the security file.", ex);
            SecurityContextHolder.clearContext();
            writeJsonError(response, HttpStatus.INTERNAL_SERVER_ERROR, "system_error", ex.getMessage());
        }
    }

    // TODO: ProblemDetail準拠にする
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
