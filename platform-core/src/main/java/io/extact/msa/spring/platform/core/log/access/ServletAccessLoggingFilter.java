package io.extact.msa.spring.platform.core.log.access;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.qos.logback.access.common.PatternLayout;
import ch.qos.logback.access.common.spi.AccessEvent;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.access.common.spi.ServerAdapter;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.OptionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @see https://logback.qos.ch/manual/layouts.html#AccessPatternLayout
 * @see https://logback.qos.ch/recipes/captureHttp.html
 */
@RequiredArgsConstructor
@Slf4j(topic = "ACCESS-LOG")
public class ServletAccessLoggingFilter extends OncePerRequestFilter {

    private static final String LAYOUT_PATTERN_CONFIG_KEY = "rms.log.access.rms-option.layout-pattern";
    private static final List<String> CONTENT_LENGTH_PATTERN_KEYWORDS = List.of("%b", "%B", "%bytesSent");

    private final Environment environment;
    private PatternLayout layout;
    private boolean hasContentLengthPattern;

    @Override
    protected void initFilterBean() throws ServletException {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        String pattern = getEnvironment().getProperty(LAYOUT_PATTERN_CONFIG_KEY);
        try {
            pattern = OptionHelper.substVars(pattern, context);
        } catch (ScanException e) {
            throw new IllegalStateException(e);
        }

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(context);

        /* ---
         * messageの最後に改行が入らないようにPatternLayoutのコンストラクタで
         * 設定されているEnsureLineSeparationインスタンスをクリアする
         */
        patternLayout.setPostCompileProcessor(null);

        patternLayout.setPattern(pattern);
        patternLayout.start();

        this.layout = patternLayout;
        this.hasContentLengthPattern = CONTENT_LENGTH_PATTERN_KEYWORDS.stream().anyMatch(pattern::contains);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        long requestStartTime = System.currentTimeMillis();
        ContentLengthAwareResponse awareResponse = hasContentLengthPattern
                ? new ContentLengthAwareResponseWrapper(response)
                : new NopContentLengthAwareResponseWrapper(response);
        try {
            chain.doFilter(request, awareResponse);
        } finally {
            outputLog(request, awareResponse, requestStartTime);
        }
    }

    @Override
    protected Environment createEnvironment() {
        return environment;
    }

    private void outputLog(HttpServletRequest request, ContentLengthAwareResponse response,
            long requestStartTime) {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ServerAdapter adapter = new ServletServerAdapter(response, requestStartTime);
        IAccessEvent event = new AccessEvent(context, request, response, adapter);

        String message = layout.doLayout(event);
        log.info(message);
    }
}
