package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Created by RezaPramudhika on 8/19/2017.
 */

public class Sales {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("member_code")
    @Expose
    private String memberCode;
    @SerializedName("bill_number")
    @Expose
    private int billNumber;
    @SerializedName("member_name")
    @Expose
    private String memberName;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("disc")
    @Expose
    private int disc;
    @SerializedName("tax")
    @Expose
    private int tax;
    @SerializedName("grand_total")
    @Expose
    private int grandTotal;
    @SerializedName("company_code")
    @Expose
    private String companyCode;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    private int maxBillNumber;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDisc() {
        return disc;
    }

    public void setDisc(int disc) {
        this.disc = disc;
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getMaxBillNumber() {
        return maxBillNumber;
    }

    public void setMaxBillNumber(int maxBillNumber) {
        this.maxBillNumber = maxBillNumber;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
