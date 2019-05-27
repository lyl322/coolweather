package com.example.kuou.Util;

import android.text.TextUtils;

import com.example.kuou.Weathre_Activity;
import com.example.kuou.db.City;
import com.example.kuou.db.Contry;
import com.example.kuou.db.Province;
import com.example.kuou.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {

    public static boolean ParseProvince(String data) throws Exception{
        if(TextUtils.isEmpty(data)){
            return false;
        }
        JSONArray array=new JSONArray(data);
        for(int i=0;i<array.length();i++){
            JSONObject object=array.getJSONObject(i);
            Province province=new Province();
            province.setProviceName(object.getString("name"));
            province.setProvinceCode(object.getInt("id"));
            province.save();
        }
        return true;
    }

    public static boolean ParseCity(String data,int provinceCodeId) throws Exception{
        if(TextUtils.isEmpty(data)){
            return false;
        }
        JSONArray array=new JSONArray(data);
        for(int i=0;i<array.length();i++){
            JSONObject object=array.getJSONObject(i);
            City city=new City();
            city.setCityName(object.getString("name"));
            city.setCityCode(object.getInt("id"));
            city.setProvinceCodeId(provinceCodeId);
            city.save();
        }
        return true;
    }

    public static boolean ParseContry(String data,int cityCodeId) throws Exception{
        if(TextUtils.isEmpty(data)){
            return false;
        }
        JSONArray array=new JSONArray(data);
        for(int i=0;i<array.length();i++){
            JSONObject object=array.getJSONObject(i);
            Contry contry=new Contry();
            contry.setContryName(object.getString("name"));
            contry.setWeatherId(object.getString("weather_id"));
            contry.setCityCodeId(cityCodeId);
            contry.save();
        }
        return true;
    }

//****
    public static Weather ParseWeather(String data) throws Exception{
        JSONObject object=new JSONObject(data);
        JSONArray array=object.getJSONArray("HeWeather");
        String weather=array.getJSONObject(0).toString();
        return new Gson().fromJson(weather,Weather.class);
    }
}
