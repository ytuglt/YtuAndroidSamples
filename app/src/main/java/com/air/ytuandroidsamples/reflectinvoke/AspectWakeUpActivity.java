package com.air.ytuandroidsamples.reflectinvoke;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.air.aspectj.Animal;
import com.air.ytuandroidsamples.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AspectWakeUpActivity extends AppCompatActivity {
    private static final String TAG = "glttest";


    private WakeUpAction wakeUpAction;
    private final int MAX_TIMES = 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WakeUp wakeUp = new WakeUp();
        getReflectMethod(wakeUp);

        Animal animal = new Animal();

        findViewById(R.id.button).setOnClickListener(v -> {
            Log.e(TAG, "onCreate: view clicked");
            wakeUp.wakeUp();
            animal.fly();
        });


    }

    private void getReflectMethod(Object object){
        long startTime = SystemClock.elapsedRealtime();

        Method[] declaredMethods = object.getClass().getDeclaredMethods();
        for (Method method: declaredMethods){
            WakeUpActor annotation = method.getAnnotation(WakeUpActor.class);
            if (annotation != null){
                wakeUpAction = new WakeUpAction(object, method);
            }
        }
        Log.e(TAG, "onCreate: totaltime3 : "  +  (SystemClock.elapsedRealtime() - startTime));

    }


    private static class WakeUpAction{
        Object object;
        Method method;

        public WakeUpAction(@NonNull Object object, @NonNull Method method){
            this.object = object;
            this.method = method;
            this.method.setAccessible(true);
        }

        public void doAction(){
            try {
                method.invoke(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


}