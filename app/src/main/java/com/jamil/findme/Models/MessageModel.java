package com.jamil.findme.Models;

import java.util.Date;

public class MessageModel {
    String message, senderName, messageId;
    String time;

    public MessageModel(String message, String senderName, String messageId, String time) {
        this.message = message;
        this.senderName = senderName;
        this.messageId = messageId;
        this.time = time;
    }

    public MessageModel() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
