package com.buc.gradution.View.Fragment.Doctor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.Adapter.Doctor.DoctorChatsRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DoctorChatFragment extends Fragment {
    private TextView noMessagesText;
    private RecyclerView recyclerView;
    private ArrayList<MessageModel> messages;
    private DoctorChatsRecyclerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_chat,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        adapter = new DoctorChatsRecyclerAdapter();
        Gson gson = new Gson();
        String json = view.getContext().getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        DoctorModel doctor = gson.fromJson(json,DoctorModel.class);
        FirebaseService.getFirebaseDatabase().getReference("Message-Doctor")
                .child(doctor.getId())
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
                                            noMessagesText.setVisibility(View.INVISIBLE);
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
        noMessagesText = view.findViewById(R.id.no_messages_text);
        recyclerView = view.findViewById(R.id.recycler_view);
    }
}