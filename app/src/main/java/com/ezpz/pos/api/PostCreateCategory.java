package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 9/16/2017.
 */

public interface PostCreateCategory {
    @FormUrlEncoded
    @POST("api/v1/add-category")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Field("category_name") String categoryName,
            @Field("company_code") String companyCode
    );
}
