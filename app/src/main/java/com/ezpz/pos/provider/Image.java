package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RezaPramudhika on 8/19/2017.
 */

public class Image {

    @SerializedName("picture")
    @Expose
    private String picture;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
