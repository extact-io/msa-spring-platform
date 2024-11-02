package io.extact.msa.spring.platform.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.debug.DebugConfig;
import io.extact.msa.spring.platform.core.env.EnvConfig;
import io.extact.msa.spring.platform.core.health.HealthConfig;
import io.extact.msa.spring.platform.core.log.LogConfig;

/**
 * coreモジュールでConditionalでON/OFFの機能があるコンポーネントを
 * まとめて登録するコンフィグ定義。
 */
@Configuration(proxyBeanMethods = false)
@Import({
    DebugConfig.class,
    EnvConfig.class,
    HealthConfig.class,
    LogConfig.class
})
public class CoreConfig {

}
