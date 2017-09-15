package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by RezaPramudhika on 9/14/2017.
 */

public interface GetBusinessList {
    @GET("api/v1/get-business-list")
    Call<Respon> setVar(
            @Query("id") Integer userId
    );
}
