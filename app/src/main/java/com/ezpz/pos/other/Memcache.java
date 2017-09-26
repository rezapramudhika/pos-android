package com.ezpz.pos.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ezpz.pos.provider.MailConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ezpz.pos.provider.BusinessCategory;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Company;
import com.ezpz.pos.provider.Product;
import com.ezpz.pos.provider.Staff;
import com.ezpz.pos.provider.User;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by RezaPramudhika on 8/17/2017.
 */

public class Memcache {

    private String PREF_NAME = "POS";
    private SharedPreferences pref;
    private int PRIVATE_MODE = 0;
    private Editor editor;
    private Context context;

    private static final String USER_KEY = "user_key";
    private static final String BUSINESS_CATEGORY_KEY = "business_category_key";
    private static final String PRODUCT_CATEGORY_KEY = "product_category_key";
    private static final String PRODUCT_KEY = "products_key";
    private static final String PRODUCT_DETAIL_KEY = "products_detail_key";
    private static final String STAFF_KEY = "staff_key";
    private static final String COMPANY_KEY = "company_key";
    private static final String MAIL_KEY = "mail_key";

    public Memcache (Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(USER_KEY, json);
        editor.commit();
    }

    public User getUser() {
        Gson gson = new Gson();
        String json = pref.getString(USER_KEY, "");
        return gson.fromJson(json, User.class);
    }

    public void setProductDetail(Product productDetail) {
        Gson gson = new Gson();
        String json = gson.toJson(productDetail);
        editor.putString(PRODUCT_DETAIL_KEY, json);
        editor.commit();
    }

    public Product getProductDetail() {
        Gson gson = new Gson();
        String json = pref.getString(PRODUCT_DETAIL_KEY, "");
        return gson.fromJson(json, Product.class);
    }

    public void setCompany(Company company) {
        Gson gson = new Gson();
        String json = gson.toJson(company);
        editor.putString(COMPANY_KEY, json);
        editor.commit();
    }

    public Company getCompany() {
        Gson gson = new Gson();
        String json = pref.getString(COMPANY_KEY, "");
        return gson.fromJson(json, Company.class);
    }


    public void setBusinessCategory(List<BusinessCategory> businessCategory) {
        Gson gson = new Gson();
        String json = gson.toJson(businessCategory);
        editor.putString(BUSINESS_CATEGORY_KEY, json);
        editor.commit();
    }

    public List<BusinessCategory> getBusinessCategory() {
        Gson gson = new Gson();
        String json = pref.getString(BUSINESS_CATEGORY_KEY, "");
        Type type = new TypeToken<List<BusinessCategory>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setProductCategory(List<Category> productCategory) {
        Gson gson = new Gson();
        String json = gson.toJson(productCategory);
        editor.putString(PRODUCT_CATEGORY_KEY, json);
        editor.commit();
    }

    public List<Category> getProductCategory() {
        Gson gson = new Gson();
        String json = pref.getString(PRODUCT_CATEGORY_KEY, "");
        Type type = new TypeToken<List<Category>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setProduct(List<Product> product) {
        Gson gson = new Gson();
        String json = gson.toJson(product);
        editor.putString(PRODUCT_KEY, json);
        editor.commit();
    }

    public List<Product> getProduct() {
        Gson gson = new Gson();
        String json = pref.getString(PRODUCT_KEY, "");
        Type type = new TypeToken<List<Product>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setStaff(List<Staff> staff) {
        Gson gson = new Gson();
        String json = gson.toJson(staff);
        editor.putString(STAFF_KEY, json);
        editor.commit();
    }

    public List<Staff> getStaff() {
        Gson gson = new Gson();
        String json = pref.getString(STAFF_KEY, "");
        Type type = new TypeToken<List<Staff>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setMailConfig(MailConfiguration mailConfig) {
        Gson gson = new Gson();
        String json = gson.toJson(mailConfig);
        editor.putString(MAIL_KEY, json);
        editor.commit();
    }

    public MailConfiguration getMailConfig() {
        Gson gson = new Gson();
        String json = pref.getString(MAIL_KEY, "");
        return gson.fromJson(json, MailConfiguration.class);
    }


    public void logout() {
        editor.clear();
        editor.commit();
    }
}
