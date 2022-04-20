package com.air.testjavapoet.factorys;

import android.util.Log;

import com.air.javapoetannotator.Factory;

@Factory(ids = {1}, superClass = IFruit.class)
public class Apple implements IFruit {
    @Override
    public void produce() {
        Log.d("AnnotationDemo", "生成苹果");
    }
}
