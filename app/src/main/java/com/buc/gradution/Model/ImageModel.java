package com.buc.gradution.Model;

import com.google.gson.annotations.SerializedName;

public class ImageModel {
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;

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
}
