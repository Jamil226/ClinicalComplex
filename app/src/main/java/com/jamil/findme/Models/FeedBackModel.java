package com.jamil.findme.Models;

public class FeedBackModel {
    String subject,message,userName,email,uid,fid;

    public FeedBackModel(String fid,String subject, String message, String userName, String email, String uid) {
        this.subject = subject;
        this.message = message;
        this.userName = userName;
        this.email = email;
        this.uid = uid;
        this.fid = fid;
    }

    public FeedBackModel() {
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
