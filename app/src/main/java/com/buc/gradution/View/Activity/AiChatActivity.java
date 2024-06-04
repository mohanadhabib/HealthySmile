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
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Interface.ChatBotInterface;
import com.buc.gradution.Model.ChatBotMessagesModel;
import com.buc.gradution.Model.ChatBotResponseModel;
import com.buc.gradution.Model.UserAiChatMessageModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.NetworkService;
import com.buc.gradution.Service.RetrofitService;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                getChatbotResponse(textMsg);
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
    private void getChatbotResponse(String message){
        HashMap<String, Object> body = new HashMap<>();
        body.put("conversation_id","123");
        body.put("bot_id","7371911820535742470");
        body.put("user","123333333");
        body.put("query",message);
        body.put("stream",false);
        UserAiChatMessageModel userMessage = new UserAiChatMessageModel(1,message);
        messages.add(userMessage);
        addMessagesToHistory();
        adapter.setMessages(messages);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messages.size()-1);
        RetrofitService.getRetrofit("https://api.coze.com/open_api/v2/")
                .create(ChatBotInterface.class)
                .getResponse(body,"Bearer "+Constant.token)
                .enqueue(new Callback<ChatBotResponseModel>() {
                    @Override
                    public void onResponse(Call<ChatBotResponseModel> call, Response<ChatBotResponseModel> response) {
                        progress.setVisibility(View.INVISIBLE);
                        String res = "";
                        for(ChatBotMessagesModel messageModel : response.body().getMessages()){
                            if(messageModel.getType().equals("answer")){
                                res = messageModel.getContent();
                            }
                        }
                        UserAiChatMessageModel aiMessage = new UserAiChatMessageModel(0,res);
                        messages.add(aiMessage);
                        addMessagesToHistory();
                        adapter.setMessages(messages);
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(messages.size()-1);
                    }

                    @Override
                    public void onFailure(Call<ChatBotResponseModel> call, Throwable throwable) {
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AiChatActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
//    private void generateGeminiResponse(String message){
//        UserAiChatMessageModel userMessage = new UserAiChatMessageModel(1,message);
//        messages.add(userMessage);
//        addMessagesToHistory();
//        adapter.setMessages(messages);
//        recyclerView.setAdapter(adapter);
//        recyclerView.scrollToPosition(messages.size()-1);
//        GenerativeModel gm = new GenerativeModel("gemini-pro", Constant.geminiApiKey);
//        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//        Content content = new Content.Builder()
//                .addText(message)
//                .build();
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//            @Override
//            public void onSuccess(GenerateContentResponse result) {
//                progress.setVisibility(View.INVISIBLE);
//                UserAiChatMessageModel aiMessage = new UserAiChatMessageModel(0,result.getText());
//                messages.add(aiMessage);
//                addMessagesToHistory();
//                adapter.setMessages(messages);
//                recyclerView.setAdapter(adapter);
//                recyclerView.scrollToPosition(messages.size()-1);
//            }
//            @Override
//            public void onFailure(Throwable t) {
//                progress.setVisibility(View.INVISIBLE);
//                Toast.makeText(AiChatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        },getMainExecutor());
//    }

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