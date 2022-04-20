package com.air.testjavapoet.factorys;

import android.util.Log;

import com.air.javapoetannotator.Factory;

@Factory(ids = {2,3}, superClass = IFruit.class)
public class Pear implements IFruit {
    @Override
    public void produce() {
        Log.d("AnnotationDemo", "生成梨子");
    }
}