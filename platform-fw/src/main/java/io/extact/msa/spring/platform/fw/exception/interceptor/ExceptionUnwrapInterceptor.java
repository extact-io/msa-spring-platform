package io.extact.msa.spring.platform.fw.exception.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletionException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Aspect
@Order(0)
public class ExceptionUnwrapInterceptor {

    @Around("@annotation(io.extact.msa.spring.platform.fw.exception.interceptor.ExceptionUnwrapAware)")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (CompletionException e) {
            throw e.getCause();
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
