package com.example.dulich;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class serviceStopPoints {
    @SerializedName("tourId")
        String tourID;
    @SerializedName("stopPoints")
    ArrayList<stopPoint> stopPoints;

    public String getTourID() {
        return tourID;
    }

    public ArrayList<stopPoint> getStopPoints() {
        return stopPoints;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public void setStopPoints(ArrayList<stopPoint> stopPoints) {
        this.stopPoints = stopPoints;
    }
}
