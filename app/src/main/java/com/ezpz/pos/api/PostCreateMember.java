package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/16/2017.
 */

public interface PostCreateMember {
    @FormUrlEncoded
    @POST("api/v1/add-new-member")
    Call<Respon> setVar(
            @Field("name") String name,
            @Field("email") String email,
            @Field("address") String address,
            @Field("contact") String contact,
            @Field("company_code") String companyCode
    );
}
