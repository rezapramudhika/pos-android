package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RezaPramudhika on 8/19/2017.
 */

public class Company {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("company_code")
    @Expose
    private String companyCode;
    @SerializedName("business_category")
    @Expose
    private String businessCategory;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("member_disc")
    @Expose
    private int memberDisc;
    @SerializedName("tax")
    @Expose
    private int tax;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getMemberDisc() {
        return memberDisc;
    }

    public void setMemberDisc(int memberDisc) {
        this.memberDisc = memberDisc;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }
}
