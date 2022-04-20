package com.air.testjavapoet.factorys;

import android.util.Log;

import com.air.javapoetannotator.Factory;

@Factory(ids = {4,5}, superClass = IFruit.class)
public class Orange implements IFruit {
    @Override
    public void produce() {
        Log.d("AnnotationDemo", "生成橙子");
    }
}