package io.extact.msa.spring.platform.core.async;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ContextSnapshotFactory.class)
public class AsyncObservationConfig {


    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> initContextRegistry() {
        return registry -> {
            ContextRegistry.getInstance().registerThreadLocalAccessor(new ObservationThreadLocalAccessor(registry));
        };
    }

    @Bean
    ContextSnapshotFactory contextSnapshotFactory() {
        return ContextSnapshotFactory.builder().build();
    }

    /**
     * {@link TaskExecutionAutoConfiguration}のTaskExecutor生成時にバインドするTaskDecoratorを返す。
     *
     * @param factory {@link ContextSnapshotFactory}
     * @return {@link Observation}を呼び先のスレッドに伝播させるDecorator
     */
    @Bean
    TaskDecorator contextPropagatingTaskDecorator(ContextSnapshotFactory factory) {
        return task -> {
            ContextSnapshot snapshot = factory.captureAll(); // 提出元スレッドの ThreadLocal 群を保存
            return () -> snapshot.wrap(task).run(); // 実行スレッドで復元して実行
        };
    }
}
