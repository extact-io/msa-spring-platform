package io.extact.msa.spring.platform.core.debug;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
public class DebugSleepInterceptor {

    @Value("${rms.debug.sleep.enable:false}")
    private boolean sleepEnable;
    @Value("${rms.debug.sleep.time:0s}")
    private Duration sleepTime;

    @Around("@annotation(io.extact.msa.spring.platform.core.debug.DebugSleepInterceptor.DebugSleep)"
            + " || within(@io.extact.msa.spring.platform.core.debug.DebugSleepInterceptor.DebugSleep *)")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        if (sleepEnable) {
            log.info("start debug sleep[{}msec]......", this.sleepTime.getSeconds());
            Thread.sleep(sleepTime);
            log.info("end debug sleep.");
        }
        return joinPoint.proceed();
    }

    @Inherited
    @Retention(RUNTIME)
    @Target({ METHOD, TYPE })
    public @interface DebugSleep {
    }
}
