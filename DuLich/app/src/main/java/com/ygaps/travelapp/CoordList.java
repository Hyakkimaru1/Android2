
package com.ygaps.travelapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordList {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("long")
    @Expose
    private double _long;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLong() {
        return _long;
    }

    public void setLong(double _long) {
        this._long = _long;
    }

}