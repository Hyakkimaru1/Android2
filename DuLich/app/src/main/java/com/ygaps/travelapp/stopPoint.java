package com.ygaps.travelapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class stopPoint {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("provinceId")
    @Expose
    private int provinceId;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("long")
    @Expose
    private double lng;
    @SerializedName("arrivalAt")
    @Expose
    private long arrivalAt;
    @SerializedName("leaveAt")
    @Expose
    private long leaveAt;
    @SerializedName("serviceTypeId")
    @Expose
    private int serviceTypeId;
    @SerializedName("minCost")
    @Expose
    private long minCost;
    @SerializedName("maxCost")
    @Expose
    private long maxCost;
    @SerializedName("serviceId")
    @Expose
    private String serviceId;


    public stopPoint(String name, String address, int provinceId, double latitude, double lat, double lng, long arrivalAt, long leaveAt, long minCost, long maxCost, String serviceId) {
        this.name = name;
        this.address = address;
        this.provinceId = provinceId;
        this.lat = lat;
        this.lng = lng;
        this.arrivalAt = arrivalAt;
        this.leaveAt = leaveAt;
        this.serviceTypeId = serviceTypeId;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.serviceId = serviceId;

    }

    public stopPoint(String id, String name,String address,int provinceId, double lat, double lng, long arrivalAt, long leaveAt, long minCost, long maxCost, int serviceTypeId, String serviceId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.provinceId = provinceId;
        this.lat = lat;
        this.lng = lng;
        this.arrivalAt = arrivalAt;
        this.leaveAt = leaveAt;
        this.serviceTypeId = serviceTypeId;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.serviceId = serviceId;
    }

    public stopPoint(String name, String address, int provinceId, double lat, double lng, long minCost, long maxCost, int serviceTypeId,String serviceId) {
        this.name = name;
        this.address = address;
        this.provinceId = provinceId;
        this.lat = lat;
        this.lng = lng;
        this.arrivalAt = 0;
        this.leaveAt = 0;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.serviceId = serviceId;
        this.serviceTypeId = serviceTypeId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(long arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public long getLeaveAt() {
        return leaveAt;
    }

    public void setLeaveAt(long leaveAt) {
        this.leaveAt = leaveAt;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public long getMinCost() {
        return minCost;
    }

    public void setMinCost(long minCost) {
        this.minCost = minCost;
    }

    public long getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(long maxCost) {
        this.maxCost = maxCost;
    }
    public String getServiceId() {
        return serviceId;
    }



}
