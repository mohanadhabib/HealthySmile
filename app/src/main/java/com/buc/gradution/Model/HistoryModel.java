package com.buc.gradution.Model;


public class HistoryModel {
    private String imgUrl;
    private String dateTime;
    public HistoryModel(){

    }
    public HistoryModel(String imgUrl, String dateTime) {
        this.imgUrl = imgUrl;
        this.dateTime = dateTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
