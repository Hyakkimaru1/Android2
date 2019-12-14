package com.ygaps.travelapp;

public class Tour_Invitation {

    int tourID;
    int hostID;
    String hostName;
    String hostPhone;
    String hostEmail;
    String hostAvt;
    int status;
    String nameTour;
    String minCost;
    String maxCost;
    String startDay;
    String endDay;
    int adults;
    int childs;
    String avtTour;
    long createOn;

    public Tour_Invitation(int tourID, int hostID, String hostName, String hostPhone, String hostEmail, String hostAvt, int status, String nameTour, String minCost, String maxCost, String startDay, String endDay, int adults, int childs, String avtTour, long createOn) {
        this.tourID = tourID;
        this.hostID = hostID;
        this.hostName = hostName;
        this.hostPhone = hostPhone;
        this.hostEmail = hostEmail;
        this.hostAvt = hostAvt;
        this.status = status;
        this.nameTour = nameTour;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.startDay = startDay;
        this.endDay = endDay;
        this.adults = adults;
        this.childs = childs;
        this.avtTour = avtTour;
        this.createOn = createOn;
    }


    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostPhone() {
        return hostPhone;
    }

    public void setHostPhone(String hostPhone) {
        this.hostPhone = hostPhone;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getHostAvt() {
        return hostAvt;
    }

    public void setHostAvt(String hostAvt) {
        this.hostAvt = hostAvt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNameTour() {
        return nameTour;
    }

    public void setNameTour(String nameTour) {
        this.nameTour = nameTour;
    }

    public String getMinCost() {
        return minCost;
    }

    public void setMinCost(String minCost) {
        this.minCost = minCost;
    }

    public String getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(String maxCost) {
        this.maxCost = maxCost;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChilds() {
        return childs;
    }

    public void setChilds(int childs) {
        this.childs = childs;
    }

    public String getAvtTour() {
        return avtTour;
    }

    public void setAvtTour(String avtTour) {
        this.avtTour = avtTour;
    }

    public long getCreateOn() {
        return createOn;
    }

    public void setCreateOn(long createOn) {
        this.createOn = createOn;
    }

}
