package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/15/2017.
 */

public interface PostUpdateProductInformation {
    @FormUrlEncoded
    @POST("api/v1/update-product-information")
    Call<Respon> setVar(
            @Field("id") int productId,
            @Field("product_code") String productCode,
            @Field("name") String name,
            @Field("category") int category,
            @Field("purchase_price") int purchasePrice,
            @Field("selling_price") int sellingPrice,
            @Field("description") String description,
            @Field("company_code") String companyCode,
            @Field("picture") String url_profile
    );
}
