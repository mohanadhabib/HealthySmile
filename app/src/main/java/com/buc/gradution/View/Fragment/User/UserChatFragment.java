package com.buc.gradution.View.Fragment.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Adapter.UserChatsRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserChatFragment extends Fragment {
    private RecyclerView recyclerView;
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
        Gson gson = new Gson();
        String json = view.getContext().getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        UserModel user = gson.fromJson(json,UserModel.class);
        FirebaseService.getFirebaseDatabase().getReference("Message")
                .child(user.getId())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                messages = new ArrayList<>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    int i = 0;
                                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                        if (i == dataSnapshot1.getChildrenCount() - 1) {
                                            MessageModel messageModel = dataSnapshot2.getValue(MessageModel.class);
                                            messages.add(messageModel);
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
    }
}
