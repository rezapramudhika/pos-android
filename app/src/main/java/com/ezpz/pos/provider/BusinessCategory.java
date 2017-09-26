package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RezaPramudhika on 8/18/2017.
 */

public class BusinessCategory {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("business_category")
    @Expose
    private String businessCategory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }
}
