package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/22/2017.
 */

public interface PostEditCustomer {
    @FormUrlEncoded
    @POST("api/v1/edit-member")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Field("id") int id,
            @Field("name") String name,
            @Field("email") String email,
            @Field("address") String address,
            @Field("contact") String contact,
            @Field("company_code") String companyCode
    );
}