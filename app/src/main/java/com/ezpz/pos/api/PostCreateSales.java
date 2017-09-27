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

public interface PostCreateSales {
    @FormUrlEncoded
    @POST("api/v1/add-sales")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Field("user_id") int idUser,
            @Field("member_code") String memberCode,
            @Field("quantity") int quantity,
            @Field("total") int total,
            @Field("disc") String disc,
            @Field("tax") String tax,
            @Field("grand_total") int grandTotal,
            @Field("cash") int cash,
            @Field("changes") int change,
            @Field("company_code") String companyCode
    );
}
