package com.buc.gradution.model;

import com.google.gson.annotations.SerializedName;

public class PointModel {
    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;

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
}
