package com.example.aws.blogapp.Models;

import com.google.firebase.database.ServerValue;

public class Post {


    private String postKey;
    private String title;
    private String description;
    private String picture;
    private String userId;
    private String userName;
    private String userPhoto;
    private String fontSize;

    private Object timeStamp ;


    public Post(String title, String description, String picture, String userId, String userName, String userPhoto,String fontSize) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.userName=userName;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.fontSize=fontSize;
    }

    public Post() {
    }


    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserId() { return userId; }

    public String getUserName() {return userName; }

    public String getfontSize() {return fontSize; }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
