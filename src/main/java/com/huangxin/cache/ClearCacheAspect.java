package com.huangxin.cache;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 延迟双删切面
 *
 * @author 黄鑫
 */
@Slf4j
@Aspect
@Component
public class ClearCacheAspect {

    @Resource
    private CacheService cacheService;

    @Pointcut("@annotation(com.huangxin.cache.ClearCache)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        log.info("===============延迟双删环绕==================");
        String methodName = proceedingJoinPoint.getSignature().getName();
        log.info("目标方法名：{}", methodName);

        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        ClearCache annotation = targetMethod.getAnnotation(ClearCache.class);

        String name = annotation.key();
        Set<String> keys = cacheService.keys("*" + name + "*");
        cacheService.delete(keys);

        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(annotation.time());
                Set<String> keys1 = cacheService.keys("*" + name + "*");
                cacheService.delete(keys1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return proceed;
    }
}
