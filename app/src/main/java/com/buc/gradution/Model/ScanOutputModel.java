package com.buc.gradution.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ScanOutputModel {
    @SerializedName("time")
    private double time;
    @SerializedName("image")
    private ImageModel image;
    @SerializedName("predictions")
    private ArrayList<PredictionModel> predictions;

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public ImageModel getImage() {
        return image;
    }

    public void setImage(ImageModel image) {
        this.image = image;
    }

    public ArrayList<PredictionModel> getPredictions() {
        return predictions;
    }

    public void setPredictions(ArrayList<PredictionModel> predictions) {
        this.predictions = predictions;
    }
}
