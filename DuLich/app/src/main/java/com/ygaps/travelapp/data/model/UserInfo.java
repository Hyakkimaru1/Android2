package com.ygaps.travelapp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class UserInfo {

    private int id;
    private String fullName = null;
    private int gender = 0;
    private String avatar = null;
    private String email = null;
    private String phone = null;
    private String address = null;
    private String dob = null;

    public UserInfo() {
    }

    public UserInfo(int id, String fullName, int gender, String avatar, String email, String phone, String address, String dob) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.avatar = avatar;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
