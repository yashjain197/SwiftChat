package com.yashjain.swiftchat.Models;

public class User {

    String name,uid,phoneNumber,profileImageUrl;

   public User(){}

   public User(String name,String uid,String phoneNumber,String profileImageUrl){
       this.name=name;
       this.uid=uid;
       this.phoneNumber=phoneNumber;
       this.profileImageUrl=profileImageUrl;
   }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
