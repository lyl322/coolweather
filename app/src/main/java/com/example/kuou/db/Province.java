package com.example.kuou.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    private int id;
    private int provinceCode;
    private String proviceName;

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProviceName() {
        return proviceName;
    }

    public void setProviceName(String proviceName) {
        this.proviceName = proviceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
