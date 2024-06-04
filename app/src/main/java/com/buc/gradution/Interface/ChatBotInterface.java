package com.buc.gradution.Interface;

import com.buc.gradution.Model.ChatBotResponseModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatBotInterface {
    @POST("chat")
    Call<ChatBotResponseModel> getResponse(@Body HashMap<String,Object> body, @Header("Authorization") String token);
}
