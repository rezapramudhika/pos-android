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

public interface PostUpdateProductStock {
    @FormUrlEncoded
    @POST("api/v1/update-product-stock")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Field("id") int productId,
            @Field("stock") int stock
    );
}
