package com.ezpz.pos.api;

import com.ezpz.pos.provider.Respon;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by RezaPramudhika on 9/22/2017.
 */

public interface PostUploadImage {
    @Multipart
    @POST("api/v1/upload-image")
    Call<Respon> setVar(
            @Header("api_token") String apiToken,
            @Part("id_product") RequestBody idUser,
            @Part MultipartBody.Part photo);
}
