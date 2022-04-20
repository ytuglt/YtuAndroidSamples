package com.air.testjavapoet.factorys;

import android.util.Log;

import com.air.javapoetannotator.Factory;

@Factory(ids = {6}, superClass = IFruit.class)
public class Persimmon implements IFruit {
    @Override
    public void produce() {
        Log.d("AnnotationDemo", "生成柿子");
    }
}