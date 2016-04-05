package com.example.moxito.dpworldtest;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Moxito on 25/03/2016.
 */
public interface GeocalizationService {

    @FormUrlEncoded
    @POST("location")
    Call<JsonElement> sendLocation(@Field("id") String truckID, @Field("latitude") String latitude, @Field("longitude") String longitude );

}
