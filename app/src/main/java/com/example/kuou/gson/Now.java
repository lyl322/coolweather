package com.example.kuou.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    public String tmp;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
