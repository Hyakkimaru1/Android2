
package com.ygaps.travelapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class getSuggest_Stoppoint {

    @SerializedName("hasOneCoordinate")
    @Expose
    private Boolean hasOneCoordinate;
    @SerializedName("coordList")
    @Expose
    private CoordList coordList;

    public Boolean getHasOneCoordinate() {
        return hasOneCoordinate;
    }

    public void setHasOneCoordinate(Boolean hasOneCoordinate) {
        this.hasOneCoordinate = hasOneCoordinate;
    }

    public CoordList getCoordList() {
        return coordList;
    }

    public void setCoordList(CoordList coordList) {
        this.coordList = coordList;
    }

}