package com.air.testjavapoet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.air.testjavapoet.factorys.IFruit;
import com.air.testjavapoet.factorys.IFruitFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IFruitFactory.create(5).produce();

//        IFruitFactory.create(5).produce();
    }


}