package com.example.tbs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static int splash_time_out = 3000;
    private SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shared = this.getSharedPreferences("com.example.tbs", Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent i;
                if(shared.getBoolean("login",false)) {
                    i = new Intent(getApplicationContext(), home.class);
                }
                else{
                    i = new Intent(getApplicationContext(), login.class);
                }
                startActivity(i);
                finish();
            }
        },splash_time_out);
    }
}