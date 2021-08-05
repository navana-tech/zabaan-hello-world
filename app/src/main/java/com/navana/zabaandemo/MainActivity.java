package com.navana.zabaandemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zabaan.sdk.Zabaan;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Zabaan.getInstance().show(this);
        Zabaan.getInstance().setScreenName("MainActivity");
        Zabaan.getInstance().setCurrentState("default");
    }
}