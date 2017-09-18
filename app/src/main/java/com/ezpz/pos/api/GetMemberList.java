package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by RezaPramudhika on 9/17/2017.
 */

public interface GetMemberList {
    @GET("api/v1/get-member")
    Call<Respon> setVar(
            @Query("company_code") String companyCode,
            @Query("search") String search
    );
}
