package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/14/2017.
 */

public interface PostCreateCompany {
    @FormUrlEncoded
    @POST("api/v1/add-new-business")
    Call<Respon> setVar(
            @Field("name") String name,
            @Field("address") String address,
            @Field("contact") String contact,
            @Field("business_category") Integer businessCategory,
            @Field("user_id") Integer userId
    );
}
