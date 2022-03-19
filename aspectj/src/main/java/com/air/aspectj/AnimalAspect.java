package com.air.aspectj;


import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AnimalAspect {

    private static final String TAG = "WakeUpAspect";

    @Pointcut("execution(* com.air.aspectj.Animal.fly(..))")
    public void callFly() {

    }

    @Around("callFly()")
    public void beforeFlyCall(ProceedingJoinPoint joinPoint) {
        Log.e(TAG, "beforeFlyCall:  around1 -> " + joinPoint.getTarget().toString() + "#" + joinPoint.getSignature().getName());

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Log.e(TAG, "beforeFlyCall:  around2 -> " + joinPoint.getTarget().toString() + "#" + joinPoint.getSignature().getName());

    }

}
