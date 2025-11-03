package io.extact.msa.spring.platform.fw.feature.observation;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import java.util.function.Predicate;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;

/**
 * レイヤー境界を自動でObservedするAspectクラス。
 * @see io.micrometer.observation.aop.ObservedAspect
 */
@Aspect
public class LayerObservationAwareAspect {

    @Value("${spring.application.name:unknwon}")
    private String applicationName;
    private final ObservationRegistry registry;
    private final Predicate<ProceedingJoinPoint> shouldSkip;

    public LayerObservationAwareAspect(ObservationRegistry registry) {
        this(registry, _ -> false);
    }

    public LayerObservationAwareAspect(ObservationRegistry registry, Predicate<ProceedingJoinPoint> shouldSkip) {
        this.registry = registry;
        this.shouldSkip = shouldSkip;
    }

    @Around("@within(io.extact.msa.spring.platform.fw.interfaces.webapi.ApiController) && execution(* *(..))")
    public Object observeRestController(ProceedingJoinPoint pjp) throws Throwable {
        return observe(pjp, "api-controller");
    }

    @Around("@within(io.extact.msa.spring.platform.fw.application.ApplicationService) && execution(* *(..))")
    public Object observeApplicationService(ProceedingJoinPoint pjp) throws Throwable {
        return observe(pjp, "application-service");
    }

    @Around("within(io.extact.msa.spring.platform.fw.domain.service.DomainService+) && execution(* *(..))")
    public Object observeDomainService(ProceedingJoinPoint pjp) throws Throwable {
        return observe(pjp, "domain-service");
    }

    @Around("""
            (
                within(io.extact.msa.spring.platform.fw.domain.repository.GenericRepository+) ||
                within(io.extact.msa.spring.platform.fw.domain.repository.IdProvider+) ||
                within(io.extact.msa.spring.platform.fw.domain.repository.DuplicationDataFinder+)
            ) && execution(* *(..))
            """)
    public Object observeDomainRepository(ProceedingJoinPoint pjp) throws Throwable {
        return observe(pjp, "domain-repostiroy");
    }

    private Object observe(ProceedingJoinPoint pjp, String layerName) throws Throwable {
        if (shouldSkip.test(pjp)) {
            return pjp.proceed();
        }

        Observation observation = createObservation(pjp, layerName);

        // 非同期呼び出し
        if (CompletionStage.class.isAssignableFrom(getMethod(pjp).getReturnType())) {
            observation.start();
            Observation.Scope scope = observation.openScope();
            try {
                Object result = pjp.proceed();
                if (result == null) {
                    stopObservation(observation, scope, null);
                    return result;
                } else {
                    CompletionStage<?> stage = (CompletionStage<?>) result;
                    return stage.whenComplete((_, error) -> stopObservation(observation, scope, error));
                }
            } catch (Throwable error) {
                stopObservation(observation, scope, error);
                throw error;
            } finally {
                scope.close();
            }
        }

        // 同期呼び出し
        return observation.observeChecked(() -> pjp.proceed());
    }

    private Observation createObservation(ProceedingJoinPoint pjp, String layerName) {
        Signature signature = pjp.getStaticPart().getSignature();
        String className = pjp.getTarget().getClass().getSimpleName();
        String name = applicationName;
        String contextualName = className + "#" + signature.getName();

        Observation observation = Observation
                .createNotStarted(name, () -> new ObservedAspect.ObservedAspectContext(pjp), registry)
                .contextualName(contextualName)
                .lowCardinalityKeyValue("class", className)
                .lowCardinalityKeyValue("method", signature.getName())
                .lowCardinalityKeyValue("layer", layerName);

        return observation;
    }

    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod(); // interfaceなど宣言側のメソッドシグニチャ
        return pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes()); // 実装側のシグニチャ
    }

    private void stopObservation(Observation observation, Observation.Scope scope, Throwable error) {
        if (error != null) {
            observation.error(error);
        }
        scope.close();
        observation.stop();
    }
}
