package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RezaPramudhika on 8/19/2017.
 */

public class Member {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("member_code")
    @Expose
    private String memberCode;
    @SerializedName("name")
    @Expose
    private String memberName;
    @SerializedName("email")
    @Expose
    private String memberEmail;
    @SerializedName("address")
    @Expose
    private String memberAddress;
    @SerializedName("contact")
    @Expose
    private String memberContact;
    @SerializedName("company_code")
    @Expose
    private String memberCompanyCode;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberAddress() {
        return memberAddress;
    }

    public void setMemberAddress(String memberAddress) {
        this.memberAddress = memberAddress;
    }

    public String getMemberContact() {
        return memberContact;
    }

    public void setMemberContact(String memberContact) {
        this.memberContact = memberContact;
    }

    public String getMemberCompanyCode() {
        return memberCompanyCode;
    }

    public void setMemberCompanyCode(String memberCompanyCode) {
        this.memberCompanyCode = memberCompanyCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
