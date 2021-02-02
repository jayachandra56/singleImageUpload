package com.example.imageupload;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api_interface {

    String JSONURL = "http://chandra.sportsontheweb.net/";

    @POST("test.php")
    @FormUrlEncoded
    Call<String> add_user(@Field("image") String image);
}
