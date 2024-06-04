package com.buc.gradution.Model;

import com.google.gson.annotations.SerializedName;

public class ChatBotMessagesModel {
    @SerializedName("role")
    private String role;
    @SerializedName("type")
    private String type;
    @SerializedName("content")
    private String content;
    @SerializedName("content_type")
    private String contentType;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
