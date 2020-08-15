package com.example.publictransporttuc.RetrofitClient;

import com.example.publictransporttuc.NetworkModels.NearbyStationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("app/pts/{university}/nearby")
    Call<NearbyStationResponse> nearbyStations(
            @Path("university") String university,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude
    );
}
