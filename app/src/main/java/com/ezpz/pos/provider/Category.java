package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RezaPramudhika on 8/19/2017.
 */

public class Category {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String categoryName;
    @SerializedName("company_code")
    @Expose
    private String categoryCompanyCode;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCompanyCode() {
        return categoryCompanyCode;
    }

    public void setCategoryCompanyCode(String categoryCompanyCode) {
        this.categoryCompanyCode = categoryCompanyCode;
    }
}
