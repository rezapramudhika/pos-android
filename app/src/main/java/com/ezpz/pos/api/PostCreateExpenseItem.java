package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/15/2017.
 */

public interface PostCreateExpenseItem {
    @FormUrlEncoded
    @POST("api/v1/add-expense-item")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Field("id") int id,
            @Field("item") String item,
            @Field("qty") int qty,
            @Field("unit") String unit,
            @Field("price") int price,
            @Field("total_price") int totalPrice,
            @Field("company_code") String companyCode,
            @Field("date") String date
    );
}
