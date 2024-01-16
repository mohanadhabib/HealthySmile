package com.buc.gradution.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PredictionModel {
    @SerializedName("x")
    private float x;
    @SerializedName("y")
    private float y;
    @SerializedName("width")
    private float width;
    @SerializedName("height")
    private float height;
    @SerializedName("confidence")
    private double confidence;
    @SerializedName("class")
    private String classType;
    @SerializedName("class_id")
    private int classId;
    @SerializedName("points")
    private ArrayList<PointModel> points;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
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

    public ArrayList<PointModel> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<PointModel> points) {
        this.points = points;
    }
}
