package com.buc.gradution.Model;

import android.net.Uri;

public class UserModel {
    private String id;
    private String name;
    private String email;
    private String type;
    private String profileImgUri;

    public UserModel(){

    }
    public UserModel(String id , String name , String email , String type , String profileImgUri){
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.profileImgUri = profileImgUri;
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
}
