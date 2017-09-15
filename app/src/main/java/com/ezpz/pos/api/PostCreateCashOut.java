package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/15/2017.
 */

public interface PostCreateCashOut {
    @FormUrlEncoded
    @POST("api/v1/add-cash")
    Call<Respon> setVar(
            @Field("total_cash") int totalCash,
            @Field("description") String description,
            @Field("company_code") String companyCode,
            @Field("type") int type
    );
}
