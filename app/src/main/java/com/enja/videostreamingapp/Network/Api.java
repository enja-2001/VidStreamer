package com.enja.videostreamingapp.Network;

import com.enja.videostreamingapp.Models.CustomOutput;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface Api {

        String BASE_URL="http://fatema.takatakind.com";
        @GET("/app_api/index.php")
        Call<CustomOutput> getData(@QueryMap Map<String, String> param);
}
