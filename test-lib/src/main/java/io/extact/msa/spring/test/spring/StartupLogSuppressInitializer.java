package io.extact.msa.spring.test.spring;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * OutputCaptureExtensionを使ったテストをする際に起動時のログ出力量を強制的に減らした
 * い場合に利用する。
 * OutputCaptureExtensionではテストメソッド開始時からキャプチャを始めるといった細かい
 * 指定ができないため、起動時のログを直接減らしたい場合に利用する。
 * 実装は{@link LoggingApplicationListener#onApplicationEvent(ApplicationEvent)}の実装を参考にしている。
 */
public class StartupLogSuppressInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    RestorableLoggingSuppressor suppressor;

    @Override
    public void initialize(ConfigurableApplicationContext context) {

        // ApplicationContextInitializedEventはContextではなく起動時にmainで生成される
        // SpringApplicationから発火されるため、Contextに登録されたListenerでは拾えない
        // なので、Listenerでsuppressするのではなく、contextの初期化時にコールバックが
        // このメソッド本体でsuppress処理を行っている
        LoggingSystem loggingSystem = LoggingSystem.get(context.getClassLoader());
        suppressor = new RestorableLoggingSuppressor(loggingSystem);
        suppressor.suppressAll(LogLevel.ERROR);

        // contextの実装側がLinsterの登録の型パラメータがを<?>で行っているため、引数に
        // 型パラメータを指定してもAvailabilityChangeEventのすべてのイベントが飛んでくる
        // このため、引数にReadinessStateを指定しても無駄なため<?>にして、メソッドの中で
        // ReadinessStateのイベントかを見ている
        context.addApplicationListener((AvailabilityChangeEvent<?> event) -> {
            if (event.getPayload() instanceof ReadinessState) {
                suppressor.restoreLogLevel();
            }
        });
    }
}