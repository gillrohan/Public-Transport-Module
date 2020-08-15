package com.example.publictransporttuc.NetworkModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NearbyStationResponse implements Serializable {

    @SerializedName("stations")
    @Expose
    private List<Station> stations = new ArrayList<>();

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

}

