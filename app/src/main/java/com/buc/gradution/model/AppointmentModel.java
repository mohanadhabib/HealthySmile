package com.buc.gradution.model;

import java.io.Serializable;

public class AppointmentModel implements Serializable {
    private String userId;
    private String userName;
    private String userEmail;
    private String userImg;
    private String doctorId;
    private String doctorName;
    private String doctorEmail;
    private String doctorImg;
    private String doctorSpec;
    private String appointmentDate;
    private String appointmentTime;
    private String stars;
    private String distance;
    private String aboutDoctor;
    public AppointmentModel(){

    }

    public AppointmentModel(String userId, String userName, String userEmail, String userImg, String doctorId, String doctorName, String doctorEmail, String doctorImg, String doctorSpec, String appointmentDate, String appointmentTime, String stars, String distance, String aboutDoctor) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImg = userImg;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
        this.doctorImg = doctorImg;
        this.doctorSpec = doctorSpec;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.stars = stars;
        this.distance = distance;
        this.aboutDoctor = aboutDoctor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public String getDoctorImg() {
        return doctorImg;
    }

    public void setDoctorImg(String doctorImg) {
        this.doctorImg = doctorImg;
    }

    public String getDoctorSpec() {
        return doctorSpec;
    }

    public void setDoctorSpec(String doctorSpec) {
        this.doctorSpec = doctorSpec;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
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

    public String getAboutDoctor() {
        return aboutDoctor;
    }

    public void setAboutDoctor(String aboutDoctor) {
        this.aboutDoctor = aboutDoctor;
    }
}
