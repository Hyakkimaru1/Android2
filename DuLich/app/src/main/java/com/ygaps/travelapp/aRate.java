package com.ygaps.travelapp;

public class aRate {


    int id;
    String name;
    int point;
    String feedback;
    String createdOn;


    public aRate(int id, String name, int point, String feedback, String createdOn) {
        this.id = id;
        this.point = point;
        this.name = name;
        this.feedback = feedback;
        this.createdOn = createdOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
