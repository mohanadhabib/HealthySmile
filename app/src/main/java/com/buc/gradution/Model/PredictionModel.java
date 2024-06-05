package com.buc.gradution.Model;

import com.google.gson.annotations.SerializedName;


public class PredictionModel {
    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("confidence")
    private double confidence;
    @SerializedName("class")
    private String classType;
    @SerializedName("class_id")
    private int classId;
    @SerializedName("detection_id")
    private String detectionId;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getDetectionId() {
        return detectionId;
    }

    public void setDetectionId(String detectionId) {
        this.detectionId = detectionId;
    }
}
