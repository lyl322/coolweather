package com.example.kuou.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    public String city;

    @SerializedName("id")
    public String weather_id;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
