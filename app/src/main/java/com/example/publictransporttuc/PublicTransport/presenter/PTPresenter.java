package com.example.publictransporttuc.PublicTransport.presenter;

import com.example.publictransporttuc.NetworkModels.NearbyStationResponse;
import com.example.publictransporttuc.PublicTransport.PTinterface;
import com.example.publictransporttuc.RetrofitClient.ApiClient;
import com.example.publictransporttuc.RetrofitClient.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PTPresenter implements PTinterface.Presenter  {

    private PTinterface.View view;

    public  PTPresenter(PTinterface.View view){
        this.view = view;
    }

    @Override
    public void fetchNearbyStations(String university, String latitude, String longitude) {
        if(university.isEmpty() && latitude.isEmpty() && longitude.isEmpty()){
            view.showErrorMessage("Fields empty");
            return;
        }
        view.onShowProgress();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<NearbyStationResponse> nearbyCall = apiInterface.nearbyStations(university,latitude,longitude);
        nearbyCall.enqueue(new Callback<NearbyStationResponse>() {
            @Override
            public void onResponse(Call<NearbyStationResponse> call, Response<NearbyStationResponse> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        if(!response.body().getStations().isEmpty()){
                            view.showSuccess(response.body().getStations());
                        }else{
                            view.showErrorMessage("No stations found");
                        }
                    }
                }else{
                    view.showErrorMessage("Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<NearbyStationResponse> call, Throwable t) {
                view.showConnectionFailure(t.getMessage());
            }
        });
    }
}
