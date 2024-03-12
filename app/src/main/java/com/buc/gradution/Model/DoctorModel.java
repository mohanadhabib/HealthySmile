package com.buc.gradution.Model;

import java.io.Serializable;

public class DoctorModel extends UserModel implements Serializable {
    private String spec;
    private String stars;
    private String distance;
    private String about;
    public DoctorModel(){

    }

    public DoctorModel(String id, String name, String email, String type, String profileImgUri, String phoneNumber, String spec, String stars, String distance, String about) {
        super(id, name, email, type, profileImgUri, phoneNumber);
        this.spec = spec;
        this.stars = stars;
        this.distance = distance;
        this.about = about;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}
