package com.example.kuou;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kuou.Json.HttpUtil;
import com.example.kuou.Util.Utility;
import com.example.kuou.gson.Forecast;
import com.example.kuou.gson.Weather;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Weathre_Activity extends AppCompatActivity {

    ScrollView weatherLayout;
    TextView cityName;
    TextView updateTime;
    TextView now_tmp;
    TextView now_info;
    LinearLayout daily_forecastList;
    TextView aqi_aqi;
    TextView aqi_pm25;
    TextView suggestion_comf;
    TextView suggestion_carWash;
    TextView suggestion_sport;
    String WeatherCodeAddress;
    ImageView backgroud_image;
    SwipeRefreshLayout refreshLayout;
    DrawerLayout drawerLayout;
    Button chose_city;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weathre_);

        Intent intent=getIntent();
        WeatherCodeAddress=intent.getStringExtra("address");
      //  WeatherCodeAddress="http://guolin.tech/api/weather?cityid=CN101040200&key=1f85fb3c0fb34a27b9404409e7b37b0e";

        weatherLayout=(ScrollView)findViewById(R.id.weatherLayout);
        cityName=(TextView)findViewById(R.id.tittle_cityName);
        updateTime=(TextView)findViewById(R.id.tittle_updateTime);
        now_tmp=(TextView)findViewById(R.id.now_tmp);
        now_info=(TextView)findViewById(R.id.now_info);
        daily_forecastList=(LinearLayout)findViewById(R.id.daily_forecastList);
        aqi_aqi=(TextView)findViewById(R.id.aqi_aqi);
        aqi_pm25=(TextView)findViewById(R.id.aqi_pm25);
        suggestion_comf=(TextView)findViewById(R.id.suggestion_comf);
        suggestion_carWash=(TextView)findViewById(R.id.suggestion_carWash);
        suggestion_sport=(TextView)findViewById(R.id.suggestion_sport);
        backgroud_image=(ImageView)findViewById(R.id.background_image);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.refresh_weather);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        chose_city=(Button)findViewById(R.id.chose_city);

     //   refreshLayout.setColorSchemeColors(R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(Weathre_Activity.this);
                Weather w=null;
                try {
                    w=Utility.ParseWeather(preferences.getString("weather",null));
                }catch (Exception e){
                    e.printStackTrace();
                }
                String weatherId=w.basic.weather_id;
                WeatherCodeAddress="http://guolin.tech/api/weather?cityid="+weatherId+"&key=1f85fb3c0fb34a27b9404409e7b37b0e";

                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(Weathre_Activity.this).edit();
                editor.putString("image",null);
                editor.putString("weather",null);
                editor.apply();
                loadImage();
                loadWeather();
                refreshLayout.setRefreshing(false);
                Toast.makeText(Weathre_Activity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });

        chose_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        showProgressDialog();
        loadWeather();
        loadImage();
        cancleProgreeDialog();
    }

    public void loadWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        final String weather=preferences.getString("weather",null);
        if(weather!=null){
            try {
                showWeather((Utility.ParseWeather(weather)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            weatherLayout.setVisibility(View.GONE);
//            Log.d(Weathre_Activity.this.toString(),WeatherCodeAddress);
            HttpUtil.sendHttp(WeatherCodeAddress,new Callback(){
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //   Log.d(this.toString(),"1111");
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Weathre_Activity.this).edit();
                    String weather_String=response.body().string();
                    editor.putString("weather",weather_String);
                    editor.apply();

                    Weather w=null;
                    try {
                        w=Utility.ParseWeather(weather_String);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final Weather finalW = w;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //   Log.d(Weathre_Activity.this.toString(),"1010110");
                            if(finalW !=null){
                                showWeather(finalW);
                            }else{
                                showWeather(finalW);
                                Toast.makeText(Weathre_Activity.this,"数据请求错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    //   Log.d(this.toString(),"2222");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Weathre_Activity.this,"数据请求失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    public void loadImage(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        String imageUrl=preferences.getString("image",null);
        if(imageUrl!=null){
            Glide.with(this).load(imageUrl).into(backgroud_image);
        }else{
            HttpUtil.sendHttp("http://guolin.tech/api/bing_pic", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                     SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(Weathre_Activity.this).edit();
                     final String imageSource=response.body().string();
                     editor.putString("image",imageSource);
                     editor.apply();
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             Glide.with(Weathre_Activity.this).load(imageSource).into(backgroud_image);
                         }
                     });
                }
            });

        }
    }

    public void showWeather(Weather weather){
    /*  Log.d(Weathre_Activity.class.toString(),"6666");
        Log.d(Weathre_Activity.class.toString(),weather.basic.city);
        Log.d(Weathre_Activity.class.toString(),weather.basic.update.updateTime);
        Log.d(Weathre_Activity.class.toString(),weather.now.tmp);
        Log.d(Weathre_Activity.class.toString(),weather.now.more.info);
        Log.d(Weathre_Activity.class.toString(),weather.aqi.city.aqi);
        Log.d(Weathre_Activity.class.toString(),weather.aqi.city.pm25);
        Log.d(Weathre_Activity.class.toString(),weather.suggestion.comfort.info);
      */
        cityName.setText(weather.basic.city);
        updateTime.setText(weather.basic.update.updateTime);
        now_tmp.setText(weather.now.tmp+"℃");
        now_info.setText(weather.now.more.info);
        aqi_aqi.setText(weather.aqi.city.aqi);
        aqi_pm25.setText(weather.aqi.city.pm25);
        suggestion_comf.setText("体感状态："+weather.suggestion.comfort.info);
        suggestion_carWash.setText("洗车建议："+weather.suggestion.carWash.info);
        suggestion_sport.setText("运动建议："+weather.suggestion.sport.info);

        daily_forecastList.removeAllViews();
        for(Forecast w:weather.forecastList){
            Log.d(Weathre_Activity.this.toString(),"one");
            View view= LayoutInflater.from(this).inflate(R.layout.weather_forecast_datason,daily_forecastList,false);
            TextView forecast_date=(TextView)view.findViewById(R.id.forecast_date);
            TextView forecast_info=(TextView)view.findViewById(R.id.forecast_info);
            TextView forecast_max=(TextView)view.findViewById(R.id.forecast_max);
            TextView forecast_min=(TextView)view.findViewById(R.id.forecast_min);
            forecast_date.setText(w.date);
            forecast_info.setText(w.more.info);
            forecast_max.setText(w.tmp.max+"℃");
            forecast_min.setText(w.tmp.min+"℃");
            daily_forecastList.addView(view);
        }

        weatherLayout.setVisibility(View.VISIBLE);
    }

    public void showProgressDialog(){
        dialog=new ProgressDialog(this);
        dialog.setTitle("loading");
        dialog.setCancelable(false);
        dialog.show();
    }

    public void cancleProgreeDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

}
