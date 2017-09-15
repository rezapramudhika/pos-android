package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RezaPramudhika on 8/18/2017.
 */

public class CashIn {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("company_code")
    @Expose
    private String companyCode;
    @SerializedName("total_cash_in")
    @Expose
    private int totalCashIn;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_at")
    @Expose
    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public int getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(int totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
