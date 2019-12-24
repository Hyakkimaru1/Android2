package com.ygaps.travelapp;

public class Message {
    private String comment; // message body
    private String avatar;
    private String name;
    private int userId;
    private boolean belongsToCurrentUser; // is this message sent by us?


    public Message(String comment, String avatar, String name, int userId, boolean belongsToCurrentUser) {
        this.comment = comment;
        this.avatar = avatar;
        this.name = name;
        this.userId = userId;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String text) {
        this.comment = text;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public void setBelongsToCurrentUser(boolean belongsToCurrentUser) {
        this.belongsToCurrentUser = belongsToCurrentUser;
    }
}