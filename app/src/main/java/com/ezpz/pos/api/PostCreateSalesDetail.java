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

public interface PostCreateSalesDetail {
    @FormUrlEncoded
    @POST("api/v1/add-sales-detail")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Field("product_id") int productId,
            @Field("product_code") String productCode,
            @Field("member_code") String memberCode,
            @Field("selling_price") int sellingPrice,
            @Field("disc") String disc,
            @Field("subtotal") int subtotal,
            @Field("company_code") String companyCode
    );
}
