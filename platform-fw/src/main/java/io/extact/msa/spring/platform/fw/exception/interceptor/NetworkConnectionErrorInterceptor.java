package io.extact.msa.spring.platform.fw.exception.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import io.extact.msa.spring.platform.fw.exception.RmsNetworkConnectionException;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Order(1)
@Slf4j
public class NetworkConnectionErrorInterceptor {

    @Value("${rms.app.name:unknown}")
    private String sourceAppName;

    @Around("@annotation(io.extact.msa.spring.platform.fw.exception.interceptor.NetworkConnectionErrorAware)")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception original) {
            Throwable test = original;
            while (!(test.getClass().getPackage().getName().equals("java.net"))) {
                test = test.getCause();
                if (test == null) {
                    throw original;
                }
            }
            String message = makeErrorInfoMessage(joinPoint);
            log.warn(message, original);
            throw new RmsNetworkConnectionException(message, original);
        }
    }

    private String makeErrorInfoMessage(ProceedingJoinPoint joinPoint) {
        String destinationClass = joinPoint.getTarget().getClass().getSimpleName();
        return "Network error on call from %s to %s".formatted(sourceAppName, destinationClass);
    }
}
