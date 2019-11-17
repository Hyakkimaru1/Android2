package com.example.dulich;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class arrayListTour {
    @SerializedName("tourId")
    String tourID;
    @SerializedName("stopPoints")
    ArrayList<stopPoint> stopPoints;
    @SerializedName("deleteIds")
    String getTourID;

}
