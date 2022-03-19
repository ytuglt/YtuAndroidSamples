package com.air.ytuandroidsamples;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "glttest";

    private final int MAX_TIMES = 1000*10;
    private InnerClass[] innerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(v -> {
            innerList = new InnerClass[MAX_TIMES];
//            long startTime = SystemClock.elapsedRealtime();
//            for (int i=0; i< MAX_TIMES; i++ ){
//                innerList[i] = new InnerClass();
//            }
//            Log.e(TAG, "onCreate: totaltime : "  +  (SystemClock.elapsedRealtime() - startTime));

            long startTime2 = SystemClock.elapsedRealtime();
            for(int i=0; i< MAX_TIMES; i++){
                innerList[i] =newInstanceByReflection();
            }
            Log.e(TAG, "onCreate: totaltime2 : "  +  (SystemClock.elapsedRealtime() - startTime2));
        });



    }

    public InnerClass newInstanceByReflection(){
        Class clazz = InnerClass.class;
        try {
            return (InnerClass) clazz.getConstructor().newInstance();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return  null;
    }

    static class InnerClass {
        private int[] tmp = new int[1000 ];
    }
}