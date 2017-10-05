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

public interface PostDeleteExpenseItem {
    @FormUrlEncoded
    @POST("api/v1/delete-expense-item")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Field("id") int id,
            @Field("cash_out_id") int cashOutId,
            @Field("total_price") int totalPrice
    );
}
