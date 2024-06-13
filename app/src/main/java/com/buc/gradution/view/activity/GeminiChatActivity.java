package com.buc.gradution.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.buc.gradution.adapter.user.UserAiChatListViewAdapter;
import com.buc.gradution.constant.Constant;
import com.buc.gradution.model.UserAiChatMessageModel;
import com.buc.gradution.R;
import com.buc.gradution.service.NetworkService;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GeminiChatActivity extends AppCompatActivity {
    private ImageView back;
    private ListView listView;
    private CircularProgressIndicator progress;
    private TextInputLayout textInputLayout;
    private final Gson gson = new Gson();
    private UserAiChatListViewAdapter adapter;
    private ArrayList<UserAiChatMessageModel> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_chat);
        initComponents();
        getMessagesHistory();
        back.setOnClickListener(v -> finish());
        textInputLayout.setEndIconOnClickListener(v -> {
            if(NetworkService.isConnected(getApplicationContext())){
                progress.setVisibility(View.VISIBLE);
                String textMsg = textInputLayout.getEditText().getText().toString();
                textInputLayout.getEditText().setText("");
                generateGeminiResponse(textMsg);
            }
            else{
                NetworkService.connectionFailed(getApplicationContext());
            }
        });
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        listView = findViewById(R.id.messages_list);
        progress = findViewById(R.id.loading);
        textInputLayout = findViewById(R.id.type_message);
    }
    private void generateGeminiResponse(String message){
        String promptMessage = "You are a dental doctor tasked with receiving initial diagnoses from patients, which include the names of their ailments and an accuracy rating. Disregard specific percentages and focus solely on the diseases' names. Begin by asking the user a series of questions aimed at helping them articulate their case. Offer personalized tips and advice to improve their condition.\n" +
                "\n" +
                "and the user response is: " + message;
        UserAiChatMessageModel userMessage = new UserAiChatMessageModel(1,message);
        messages.add(userMessage);
        addMessagesToHistory();
        adapter.setMessages(messages);
        listView.setAdapter(adapter);
        GenerativeModel gm = new GenerativeModel("gemini-pro", Constant.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(promptMessage)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                progress.setVisibility(View.INVISIBLE);
                UserAiChatMessageModel aiMessage = new UserAiChatMessageModel(0,result.getText());
                messages.add(aiMessage);
                addMessagesToHistory();
                adapter.setMessages(messages);
                listView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(GeminiChatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        },getMainExecutor());
    }

    private void getMessagesHistory(){
        String json = getApplicationContext().getSharedPreferences(Constant.GEMINI_SHARED_PREFERENCES,0).getString(Constant.GEMINI_HISTORY,"");
        if(json.equals("")){
            messages = new ArrayList<>();
        }else{
            messages = gson.fromJson(json,new TypeToken<ArrayList<UserAiChatMessageModel>>(){}.getType());
        }
        adapter = new UserAiChatListViewAdapter();
        adapter.setMessages(messages);
        listView.setAdapter(adapter);
    }
    private void addMessagesToHistory(){
        SharedPreferences.Editor shared = getApplicationContext().getSharedPreferences(Constant.GEMINI_SHARED_PREFERENCES,0).edit();
        String historyTxt = gson.toJson(messages);
        shared.putString(Constant.GEMINI_HISTORY,historyTxt);
        shared.commit();
    }
}