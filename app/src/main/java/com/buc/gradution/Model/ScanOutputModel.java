package com.buc.gradution.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ScanOutputModel {
    @SerializedName("predictions")
    private ArrayList<PredictionModel> predictions;

    public ArrayList<PredictionModel> getPredictions() {
        return predictions;
    }

    public void setPredictions(ArrayList<PredictionModel> predictions) {
        this.predictions = predictions;
    }
}
