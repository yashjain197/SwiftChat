package com.yashjain.swiftchat.Models;

public class Message {

    private String messageId,senderId,message,imageUrl;
    private long timestamp;
    private long feeling=-1;


    Message(){}

    public Message(String message,String senderId,long timestamp){
        this.message=message;
        this.senderId=senderId;
        this.timestamp=timestamp;
    }
    public String getMessageId(){
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getFeeling() {
        return feeling;
    }

    public void setFeeling(long feeling) {
        this.feeling = feeling;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
