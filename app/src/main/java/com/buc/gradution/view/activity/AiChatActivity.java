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
import com.buc.gradution.interfaceType.ChatBotInterface;
import com.buc.gradution.model.ChatBotMessagesModel;
import com.buc.gradution.model.ChatBotResponseModel;
import com.buc.gradution.model.UserAiChatMessageModel;
import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseService;
import com.buc.gradution.service.NetworkService;
import com.buc.gradution.service.RetrofitService;
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
    private ListView listView;
    private CircularProgressIndicator progress;
    private TextInputLayout textInputLayout;
    private final Gson gson = new Gson();

    private UserAiChatListViewAdapter adapter;
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
        listView = findViewById(R.id.messages_list);
        progress = findViewById(R.id.loading);
        textInputLayout = findViewById(R.id.type_message);
    }
    private void getChatbotResponse(String message){
        HashMap<String, Object> body = new HashMap<>();
        body.put("conversation_id","0"+ FirebaseService.getFirebaseAuth().getCurrentUser().getUid() +"0");
        body.put("bot_id","7379420348422815750");
        body.put("user",FirebaseService.getFirebaseAuth().getCurrentUser().getUid());
        body.put("query",message);
        body.put("stream",false);
        UserAiChatMessageModel userMessage = new UserAiChatMessageModel(1,message);
        messages.add(userMessage);
        addMessagesToHistory();
        adapter.setMessages(messages);
        listView.setAdapter(adapter);
        RetrofitService.getLongTimeOutRetrofit("https://api.coze.com/open_api/v2/")
                .create(ChatBotInterface.class)
                .getResponse(body,
                        "Bearer "+Constant.TOKEN,
                        Constant.TYPE_VALUE,
                        Constant.ACCEPT_VALUE,
                        Constant.HOST_VALUE,
                        Constant.CONNECTION_VALUE)
                .enqueue(new Callback<ChatBotResponseModel>() {
                    @Override
                    public void onResponse(Call<ChatBotResponseModel> call, Response<ChatBotResponseModel> response) {
                        progress.setVisibility(View.INVISIBLE);
                        String res;
                        if(response.body() != null){
                            for(ChatBotMessagesModel messageModel : response.body().getMessages()){
                                if(messageModel.getType().equals("answer")){
                                    res = messageModel.getContent();
                                    UserAiChatMessageModel aiMessage = new UserAiChatMessageModel(0,res);
                                    messages.add(aiMessage);
                                    addMessagesToHistory();
                                    adapter.setMessages(messages);
                                    listView.setAdapter(adapter);
                                    break;
                                }
                            }
                        }
                        else {
                            Toast.makeText(AiChatActivity.this, "Sorry, Couldn't get results", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatBotResponseModel> call, Throwable throwable) {
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AiChatActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getMessagesHistory(){
        String json = getApplicationContext().getSharedPreferences(Constant.CHAT_BOT_SHARED_PREFERENCES,0).getString(Constant.CHAT_BOT_HISTORY,"");
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
        SharedPreferences.Editor shared = getApplicationContext().getSharedPreferences(Constant.CHAT_BOT_SHARED_PREFERENCES,0).edit();
        String historyTxt = gson.toJson(messages);
        shared.putString(Constant.CHAT_BOT_HISTORY,historyTxt);
        shared.commit();
    }
}