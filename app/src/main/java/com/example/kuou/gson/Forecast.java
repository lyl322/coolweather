package com.example.kuou.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;

    @SerializedName("cond")
    public More more;

    public Tmp tmp;

    public class More{
        @SerializedName("txt_d")
        public String info;
    }

    public class Tmp{
        public String max;
        public String min;
    }
}
