package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by RezaPramudhika on 9/22/2017.
 */

public interface GetMemberDetail {
    @GET("api/v1/get-selected-member")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Query("id") int id,
            @Query("member_code") String memberCode
    );
}
