package io.extact.msa.spring.platform.fw.webapi;

import java.util.Map;
import java.util.Set;

/**
 * RMSのRESTアプリケーションで共通して設定するリソースの定義。
 * Applicaitonのabstractなサブクラスで定義したかったがMicroProfile OpenAPIのパス解決時に
 * abstractなサブクラスを@ApplicationPathの指定がないApplicationクラスとして使われてしまう
 * ため、template methodは作成せず必要なリソースを定数定義したクラスを用意することにした。
 */
public class RmsBaseApplications {

    public static final Set<Class<?>> CLASSES = Set.of(
                //RmsTypeParameterFeature.class,
                ServerExceptionMapperFeature.class
                //ServerHeaderDumpFilter.class
            );

    public static final Map<String, Object> PROPERTIES = Map.of(
                // The following keys are defined in `ServerProperties.BV_SEND_ERROR_IN_RESPONSE`
                "jersey.config.beanValidation.disable.server", true  // jerseyのJAX-RSのBeanValidationサポートをOFFにする
            );
}
