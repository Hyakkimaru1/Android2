package com.example.dulich;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class serviceStopPoints  {
    @SerializedName("tourId")
    @Expose
    String tourID;
    @SerializedName("stopPoints")
    @Expose
    ArrayList<stopPoint> stopPoints;

    public String getTourID() {
        return tourID;
    }

    public serviceStopPoints(String tourID, ArrayList<stopPoint> stopPoints) {
        this.tourID = tourID;
        this.stopPoints = stopPoints;
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
