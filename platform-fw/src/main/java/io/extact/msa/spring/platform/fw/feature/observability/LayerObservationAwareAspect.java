package io.extact.msa.spring.platform.fw.feature.observability;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LayerObservationAwareAspect extends ObservedAspect {
    public LayerObservationAwareAspect(ObservationRegistry registry) {
        super(registry);
    }
//
//    @Around("@within(io.micrometer.observation.annotation.Observed) && !@annotation(io.micrometer.observation.annotation.Observed) && execution(* *.*(..))")
//    public Object observeClass(ProceedingJoinPoint pjp) throws Throwable {
//    }

}
