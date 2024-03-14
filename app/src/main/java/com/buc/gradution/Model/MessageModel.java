package com.buc.gradution.Model;


public class MessageModel {
    private String doctorName;
    private String userName;
    private String message;
    private String receiverId;
    private String senderId;
    private String doctorEmail;
    private String userEmail;
    private String doctorImg;
    private String userImg;
    private String userId;
    private String doctorId;
    public MessageModel(){

    }

    public MessageModel(String doctorName, String userName, String message, String receiverId, String senderId, String doctorEmail, String userEmail, String doctorImg, String userImg, String userId, String doctorId) {
        this.doctorName = doctorName;
        this.userName = userName;
        this.message = message;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.doctorEmail = doctorEmail;
        this.userEmail = userEmail;
        this.doctorImg = doctorImg;
        this.userImg = userImg;
        this.userId = userId;
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDoctorImg() {
        return doctorImg;
    }

    public void setDoctorImg(String doctorImg) {
        this.doctorImg = doctorImg;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
}
