package io.extact.msa.spring.platform.fw;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.platform.fw.web.RestControllerConfig;

/**
 * coreモジュールでConditionalでON/OFFの機能があるコンポーネントを
 * まとめて登録するコンフィグ定義。
 */
@Configuration(proxyBeanMethods = false)
@Import({
    CoreConfig.class,
    RestControllerConfig.class
})
public class RmsFrameworkConfig {

}
