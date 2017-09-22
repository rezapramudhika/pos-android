package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by RezaPramudhika on 9/15/2017.
 */

public interface GetCashOutList {
    @GET("api/v1/get-cash-out")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Query("id") String companyCode
    );
}
