package com.example.kuou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this,Weathre_Activity.class);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if((preferences.getString("weather",null)!=null)){
            startActivity(intent);
        }
        Intent intent1=new Intent(this,AutoUpdate.class);
        startService(intent1);
    }
}
