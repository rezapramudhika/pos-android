package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/16/2017.
 */

public interface PostEditCompany {
    @FormUrlEncoded
    @POST("api/v1/edit-company")
    Call<Respon> setVar(
            @Field("name") String name,
            @Field("address") String address,
            @Field("contact") String contact,
            @Field("discount") String discount,
            @Field("tax") String tax,
            @Field("id") String companyId
    );
}
