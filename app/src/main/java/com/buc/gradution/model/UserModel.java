package com.buc.gradution.model;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String id;
    private String name;
    private String email;
    private String type;
    private String profileImgUri;
    private String phoneNumber;

    public UserModel(){

    }

    public UserModel(String id, String name, String email, String type, String profileImgUri, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.profileImgUri = profileImgUri;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public String getProfileImgUri() {return profileImgUri;}

    public void setProfileImgUri(String profileImgUri) {this.profileImgUri = profileImgUri;}

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
