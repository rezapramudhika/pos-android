package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by RezaPramudhika on 9/15/2017.
 */

public interface GetProductList {
    @GET("api/v1/get-product")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Query("company_code") String companyCode,
            @Query("category") String category
    );
}
