package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/14/2017.
 */

public interface PostLogin {
    @FormUrlEncoded
    @POST("api/v1/login")
    Call<Respon> setVar(
            @Field("email") String email,
            @Field("password") String password
    );
}
