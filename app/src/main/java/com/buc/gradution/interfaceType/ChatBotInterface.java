package com.buc.gradution.interfaceType;

import com.buc.gradution.model.ChatBotResponseModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatBotInterface {
    @POST("chat")
    Call<ChatBotResponseModel> getResponse(@Body HashMap<String,Object> body, @Header("Authorization") String token ,
                                           @Header("Content-Type") String type,
                                           @Header("Accept") String accept,
                                           @Header("Host") String host,
                                           @Header("Connection") String connection
                                           );
}
