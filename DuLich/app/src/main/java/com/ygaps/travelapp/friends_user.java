package com.ygaps.travelapp;

public class friends_user {

    int id;
    String fullName;
    String email;
    String phone;
    String avatar;
    boolean isHost;


    public friends_user(int id, String fullName, String email, String phone, String avatar) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    //    this.isPrivate = isPrivate;
        this.avatar = avatar;
         this.isHost = false;
    }
    public friends_user(int id, String fullName, String phone, String avatar, boolean isHost) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        //    this.isPrivate = isPrivate;
        this.avatar = avatar;
        this.isHost = isHost;
    }


    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
