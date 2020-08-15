package com.example.publictransporttuc.PublicTransport;

import com.example.publictransporttuc.NetworkModels.Station;

import java.util.List;

public interface PTinterface {
    interface  View{
        void onShowProgress();
        void onHideProgress();
        void showSuccess(List<Station> stations);
        void showErrorMessage(String message);
        void showConnectionFailure(String message);
    }

    interface  Presenter{
        void fetchNearbyStations(String university,String latitude,String longitude);
    }
}
