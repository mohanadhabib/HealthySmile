package com.buc.gradution.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatBotResponseModel {
    @SerializedName("messages")
    private List<ChatBotMessagesModel> messages;
    @SerializedName("conversation_id")
    private String conversationId;
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    public List<ChatBotMessagesModel> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatBotMessagesModel> messages) {
        this.messages = messages;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
