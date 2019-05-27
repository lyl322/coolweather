package com.example.kuou;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kuou.Json.HttpUtil;
import com.example.kuou.Util.Utility;
import com.example.kuou.gson.Weather;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdate extends Service {
    public AutoUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadWeather();
                loadimage();

                Long currcentTime= SystemClock.elapsedRealtime();
                int anHour=1000*60*60;
                Intent intent1=new Intent(AutoUpdate.this,AutoUpdate.class);
                PendingIntent pi=PendingIntent.getService(AutoUpdate.this,0,intent1,0);
                AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,currcentTime+anHour*8,pi);
            }

        }).start();

       // Intent intent2=new Intent(AutoUpdate.this,Weathre_Activity.class);
       // startActivity(intent2);

      //  Toast.makeText(this,"自动更新成功",Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }

    public void loadWeather(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        String weather=preferences.getString("weather",null);
        Weather w=null;
        if(weather!=null){
            try {
                w= Utility.ParseWeather(weather);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String weatherId=w.basic.weather_id;
            String Url="http://guolin.tech/api/weather?cityid="+weatherId+"&key=1f85fb3c0fb34a27b9404409e7b37b0e";
            HttpUtil.sendHttp(Url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                     SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdate.this).edit();
                     editor.putString("weather",response.body().string());
                     editor.apply();
                }
            });
        }
    }

    public void loadimage(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        String image=preferences.getString("image",null);
        if(image!=null){
            HttpUtil.sendHttp("http://guolin.tech/api/bing_pic", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdate.this).edit();
                    editor.putString("image",response.body().string());
                    editor.apply();
                }
            });
        }
    }

}
