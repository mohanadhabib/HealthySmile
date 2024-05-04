package com.buc.gradution.Model;

public class HistoryModel {
    private String imgUrl;
    public HistoryModel(){

    }
    public HistoryModel(String imgUrl){
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
