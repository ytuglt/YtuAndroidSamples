package com.air.ytuandroidsamples.reflectinvoke;

import android.util.Log;

public class WakeUp {
    private static final String TAG = "glttest";

    @WakeUpActor
    public void wakeUp(){
        Log.e(TAG, "wakeUp called");
    }
}
