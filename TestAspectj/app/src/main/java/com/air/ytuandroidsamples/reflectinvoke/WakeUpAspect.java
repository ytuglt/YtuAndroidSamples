package com.air.ytuandroidsamples.reflectinvoke;


import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class WakeUpAspect {

    private static final String TAG = "WakeUpAspect";

    @Pointcut("execution(* com.air.ytuandroidsamples.reflectinvoke.WakeUp.wakeUp(..))")
    public void callWakeUp() {

    }

    @Around("callWakeUp()")
    public void beforeWakeUpCall(ProceedingJoinPoint joinPoint) {
        Log.e(TAG, "beforeWakeUpCall:  around1 -> " + joinPoint.getTarget().toString() + "#" + joinPoint.getSignature().getName());

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Log.e(TAG, "beforeWakeUpCall:  around2 -> " + joinPoint.getTarget().toString() + "#" + joinPoint.getSignature().getName());

    }

}
