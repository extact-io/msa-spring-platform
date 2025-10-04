package io.extact.msa.spring.platform.core.async;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import lombok.RequiredArgsConstructor;

@Configuration(proxyBeanMethods = false)
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final AsyncTaskExecutor taskExecutor;

    @Bean
    AsyncInvoker asyncInvoker() {
        return new AsyncInvoker();
    }

    /**
     * {@link #getAsyncExecutor()}で返された非同期Executorが<code>@Async</code>で使われる。
     * SecurityContextが引き継がれるようにこのメソッドをオーバーライドしている。
     * 実体は<code>TaskExecutorConfigurations.TaskExecutorConfiguration</code>で自動構成
     * されたExecutorが使われる。
     *
     * @see https://irof.hateblo.jp/entry/2023/08/24/221215
     */
    @Override
    public Executor getAsyncExecutor() {
        return new DelegatingSecurityContextAsyncTaskExecutor(taskExecutor);
    }
}
