package com.example.kuou.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    private int id;
    private int cityCode;
    private String cityName;
    private int provinceCodeId;

    public int getProvinceCodeId() {
        return provinceCodeId;
    }

    public void setProvinceCodeId(int provinceCodeId) {
        this.provinceCodeId = provinceCodeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }
}
