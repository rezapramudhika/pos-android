package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/15/2017.
 */

public interface PostDeleteCashOut {
    @FormUrlEncoded
    @POST("api/v1/delete-cash")
    Call<Respon> setVar(
            @Field("id") int totalCash,
            @Field("type") int type
    );
}
