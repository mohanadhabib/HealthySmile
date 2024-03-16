package com.buc.gradution.View.Activity.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.Adapter.User.UserDoctorMessageRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserChatActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private DoctorModel doctor;
    private UserModel user;
    private ImageView back,phone,video;
    private TextView doctorName,noData;
    private RecyclerView recyclerView;
    private TextInputLayout typeMessage;
    private MaterialButton sendMessage;
    private UserDoctorMessageRecyclerAdapter adapter;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        initComponents();
        String json = getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        doctor = (DoctorModel) getIntent().getSerializableExtra(Constant.OBJECT);
        user = gson.fromJson(json,UserModel.class);
        back.setOnClickListener(v -> {
            finish();
        });
        doctorName.setText(doctor.getName());
        sendMessage.setOnClickListener(v ->{
            String message = typeMessage.getEditText().getText().toString();
            if(!message.isEmpty()){
                if(NetworkService.isConnected(getApplicationContext())){
                    sendMessageToDB(message);
                }
                else{
                    NetworkService.connectionFailed(getApplicationContext());
                }
            }
        });
        adapter = new UserDoctorMessageRecyclerAdapter(user);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        getMessageFirstTime(doctor.getId());
        getMessages(doctor.getId());
    }
    private void sendMessageToDB(String message){
        MessageModel messageModel = new MessageModel(doctor.getName(),user.getName(),message,doctor.getId(),user.getId(),doctor.getEmail(),user.getEmail(),doctor.getProfileImgUri(),user.getProfileImgUri(),user.getId(),doctor.getId());
        String ref = doctor.getId();
        FirebaseService.getFirebaseDatabase().getReference("Message-User").child(user.getId())
                .child(doctor.getId())
                .push()
                .setValue(messageModel)
                .addOnSuccessListener(x ->{
                    FirebaseService.getFirebaseDatabase().getReference("Message-Doctor").child(doctor.getId())
                            .child(user.getId())
                            .push()
                            .setValue(messageModel)
                            .addOnSuccessListener(v ->{
                                typeMessage.getEditText().getText().clear();
                                getMessages(ref);
                            })
                            .addOnFailureListener(e ->{
                                Toast.makeText(getApplicationContext(),"Sorry, Couldn't send message",Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(getApplicationContext(),"Sorry, Couldn't send message",Toast.LENGTH_SHORT).show();
                });
    }
    private void getMessages(String ref){
        FirebaseService.getFirebaseDatabase().getReference("Message-User").child(user.getId())
                .child(ref)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<MessageModel> messages = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessageModel message = snapshot1.getValue(MessageModel.class);
                            messages.add(message);
                        }
                        adapter.setMessages(messages);
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(messages.size()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getMessageFirstTime(String ref){
        FirebaseService.getFirebaseDatabase().getReference("Message-User").child(user.getId())
                .child(ref)
                .get()
                .addOnSuccessListener(snapshot ->{
                    ArrayList<MessageModel> messages = new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        MessageModel message = snapshot1.getValue(MessageModel.class);
                        messages.add(message);
                    }
                    adapter.setMessages(messages);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(messages.size()-1);
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        doctorName = findViewById(R.id.doctor_name);
        video = findViewById(R.id.video_call);
        phone = findViewById(R.id.phone_call);
        noData = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.messages_recycler);
        typeMessage = findViewById(R.id.type_message);
        sendMessage = findViewById(R.id.send_message);
    }
}