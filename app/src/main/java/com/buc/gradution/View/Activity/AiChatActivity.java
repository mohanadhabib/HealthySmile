package com.buc.gradution.View.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Adapter.User.UserAIChatsRecyclerAdapter;
import com.buc.gradution.Adapter.User.UserAppointmentsRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.AppointmentModel;
import com.buc.gradution.Model.UserAiChatMessageModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.NetworkService;
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

public class AiChatActivity extends AppCompatActivity {

    private ImageView back;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progress;
    private TextInputLayout textInputLayout;
    private Gson gson = new Gson();
    private UserAIChatsRecyclerAdapter adapter;
    private ArrayList<UserAiChatMessageModel> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);
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
        recyclerView = findViewById(R.id.messages_recycler);
        progress = findViewById(R.id.loading);
        textInputLayout = findViewById(R.id.type_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
    }
    private void generateGeminiResponse(String message){
        UserAiChatMessageModel userMessage = new UserAiChatMessageModel(1,message);
        messages.add(userMessage);
        addMessagesToHistory();
        adapter.setMessages(messages);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messages.size()-1);
        GenerativeModel gm = new GenerativeModel("gemini-pro", Constant.geminiApiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(message)
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
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(messages.size()-1);
            }
            @Override
            public void onFailure(Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(AiChatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        adapter = new UserAIChatsRecyclerAdapter();
        adapter.setMessages(messages);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messages.size()-1);
    }
    private void addMessagesToHistory(){
        SharedPreferences.Editor shared = getApplicationContext().getSharedPreferences(Constant.GEMINI_SHARED_PREFERENCES,0).edit();
        String historyTxt = gson.toJson(messages);
        shared.putString(Constant.GEMINI_HISTORY,historyTxt);
        shared.commit();
    }
}