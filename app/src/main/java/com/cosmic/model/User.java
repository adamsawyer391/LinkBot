package com.cosmic.model;

public class User {

    private String userID, email, username, fullname;

    public User() {

    }

    public User(String userID, String email, String username, String fullname) {
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.fullname = fullname;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
