package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Created by RezaPramudhika on 8/19/2017.
 */

public class SalesDetail {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("sales_id")
    @Expose
    private int salesId;
    @SerializedName("bill_number")
    @Expose
    private int billNumber;
    @SerializedName("member_code")
    @Expose
    private String memberCode;
    @SerializedName("product_id")
    @Expose
    private int productId;
    @SerializedName("selling_price")
    @Expose
    private int sellingPrice;
    @SerializedName("disc")
    @Expose
    private int disc;
    @SerializedName("sub_total")
    @Expose
    private int subTotal;
    @SerializedName("company_code")
    @Expose
    private String companyCode;
    @SerializedName("product_name")
    @Expose
    private String productName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(int sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getDisc() {
        return disc;
    }

    public void setDisc(int disc) {
        this.disc = disc;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
