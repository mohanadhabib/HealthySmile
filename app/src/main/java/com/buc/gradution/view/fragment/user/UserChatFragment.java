package com.buc.gradution.view.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.adapter.user.UserChatsRecyclerAdapter;
import com.buc.gradution.constant.Constant;
import com.buc.gradution.model.MessageModel;
import com.buc.gradution.model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseSecurity;
import com.buc.gradution.service.FirebaseService;
import com.buc.gradution.view.activity.AiChatActivity;
import com.buc.gradution.view.activity.GeminiChatActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserChatFragment extends Fragment {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton chatBtn,geminiBtn;
    private TextView noMessageText;
    private final Gson gson = new Gson();
    private ArrayList<MessageModel> messages;
    private UserChatsRecyclerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_chat,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        adapter = new UserChatsRecyclerAdapter();
        String json = view.getContext().getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        UserModel user = gson.fromJson(json,UserModel.class);
        chatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), AiChatActivity.class);
            startActivity(intent);
        });
        geminiBtn.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity().getApplicationContext(), GeminiChatActivity.class);
            startActivity(intent);
        });
        FirebaseService.getFirebaseDatabase().getReference("Message-User")
                .child(user.getId())
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                messages = new ArrayList<>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    int i = 0;
                                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                        if (i == dataSnapshot1.getChildrenCount() - 1) {
                                            MessageModel messageModel;
                                            try {
                                                messageModel = security.decryptMessage(dataSnapshot2.getValue().toString());
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                            messages.add(messageModel);
                                            noMessageText.setVisibility(View.INVISIBLE);
                                            adapter.setMessages(messages);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                                        }
                                        i++;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }
    private void initComponents(View view){
        recyclerView = view.findViewById(R.id.recycler_view);
        noMessageText = view.findViewById(R.id.no_messages_text);
        chatBtn = view.findViewById(R.id.chat_btn);
        geminiBtn = view.findViewById(R.id.gemini_btn);
    }
//    private void getMessagesHistory(){
//        String json = getActivity().getApplicationContext().getSharedPreferences(Constant.MESSAGES_SHARED_PREFERENCES,0).getString(Constant.MESSAGES_HISTORY,"");
//        if(json.equals("")){
//            messages = new ArrayList<>();
//        }else{
//            messages = gson.fromJson(json,new TypeToken<ArrayList<MessageModel>>(){}.getType());
//        }
//        adapter = new UserChatsRecyclerAdapter();
//        adapter.setMessages(messages);
//        recyclerView.setAdapter(adapter);
//        recyclerView.scrollToPosition(messages.size()-1);
//    }
//    private void addMessagesToHistory(){
//        SharedPreferences.Editor shared = getActivity().getApplicationContext().getSharedPreferences(Constant.MESSAGES_SHARED_PREFERENCES,0).edit();
//        String historyTxt = gson.toJson(messages);
//        shared.putString(Constant.MESSAGES_HISTORY,historyTxt);
//        shared.commit();
//    }
}