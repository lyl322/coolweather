package com.example.kuou.db;

import org.litepal.crud.DataSupport;

public class Contry extends DataSupport {
    private int id;
    private String contryName;
    private String weatherId;
    private int cityCodeId;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContryName() {
        return contryName;
    }

    public void setContryName(String contryName) {
        this.contryName = contryName;
    }

    public int getCityCodeId() {
        return cityCodeId;
    }

    public void setCityCodeId(int cityCodeId) {
        this.cityCodeId = cityCodeId;
    }
}
