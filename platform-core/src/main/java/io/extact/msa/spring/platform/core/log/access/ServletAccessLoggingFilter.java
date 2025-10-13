package io.extact.msa.spring.platform.core.log.access;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.qos.logback.access.common.PatternLayout;
import ch.qos.logback.access.common.spi.AccessEvent;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.access.common.spi.ServerAdapter;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.OptionHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServletAccessLoggingFilter extends OncePerRequestFilter {

    private PatternLayout layout;

    @Override
    protected void initFilterBean() throws ServletException {

        // TODO 設定から書式を持ってくるようにする
        // TODO layoutのconverterにContentLengthConverterがない場合はCountableのWrapperを使わないようにする
        // TODO ログの先頭に沢山共通情報がでないよういにAppenderを分ける(2重ででないようにaddivitityはfalseにする)
        // TODO どのアクセスログを有効にするかConditionalOnを考える

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        String pattern = "[ACCESS] %h %l %u %r %t{yyyy-MM-dd HH:mm:ss.SSS} %s %b %D ms";
        try {
            // TODO: 何をresolveしているの確認しておく
            pattern = OptionHelper.substVars(pattern, context);
        } catch (ScanException e) {
            throw new IllegalStateException(e);
        }

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(context);

        // messageの最後に改行が入らないようにPatternLayoutのコンストラクタで
        // 設定されているEnsureLineSeparationインスタンスをクリアする
        patternLayout.setPostCompileProcessor(null);

        patternLayout.setPattern(pattern);
        patternLayout.start();

        this.layout = patternLayout;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        long requestStartTime = System.currentTimeMillis();
        CountableServeletResponseWrapper wrappedResponse = new CountableServeletResponseWrapper(response);
        try {
            chain.doFilter(request, wrappedResponse);
        } finally {
            outputLog(request, wrappedResponse, requestStartTime);
        }
    }

    private void outputLog(HttpServletRequest request, CountableServeletResponseWrapper response,
            long requestStartTime) {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ServerAdapter adapter = new ServletServerAdapter(response, requestStartTime);
        IAccessEvent event = new AccessEvent(context, request, response, adapter);

        String message = layout.doLayout(event);
        log.info(message);
    }
}
