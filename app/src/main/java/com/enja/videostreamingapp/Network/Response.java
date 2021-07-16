package com.enja.videostreamingapp.Network;

import android.util.Log;

import com.enja.videostreamingapp.Callbacks.ResponseCallback;
import com.enja.videostreamingapp.Models.CustomOutput;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class Response {
    public void getResponse(ResponseCallback responseCallback){

        //initializing the query parameters
        Map<String, String> queryParameter = new HashMap<>();
        queryParameter.put("p", "showAllVideos");

        Call<CustomOutput> call = ApiClient.getClient().create(Api.class).getData(queryParameter);

        call.enqueue(new Callback<CustomOutput>() {
            @Override
            public void onResponse(Call<CustomOutput> call, retrofit2.Response<CustomOutput> response) {

                try {
                    if (response.body() != null) {
                        Log.d("response", ""+response.body().getMsg().size());
                        responseCallback.onResponseRetrieved(response.body());//pass the CustomOutput object inside callback
                    } else {
                        Log.d("response", "null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CustomOutput> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
            }
        });
    }
}
