package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RezaPramudhika on 10/4/2017.
 */

public class ExpenseDetail {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("cash_out_id")
    @Expose
    private int cashOutId;
    @SerializedName("item")
    @Expose
    private String itemName;
    @SerializedName("qty")
    @Expose
    private int qty;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("company_code")
    @Expose
    private String companyCode;
    @SerializedName("date")
    @Expose
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCashOutId() {
        return cashOutId;
    }

    public void setCashOutId(int cashOutId) {
        this.cashOutId = cashOutId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}

