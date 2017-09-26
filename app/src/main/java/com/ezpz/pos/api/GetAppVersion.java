package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by RezaPramudhika on 9/17/2017.
 */

public interface GetAppVersion {
    @GET("api/v1/get-app-version")
    Call<Respon> setVar(
    );
}
