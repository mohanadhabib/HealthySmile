package com.buc.gradution.View.Activity.User;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.buc.gradution.Service.FirebaseSecurity;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserDoctorChatActivity extends AppCompatActivity {
    private final FirebaseSecurity security = new FirebaseSecurity();
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
        setContentView(R.layout.activity_user_doctor_chat);
        initComponents();
        doctor = (DoctorModel) getIntent().getSerializableExtra(Constant.OBJECT);
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(UserDoctorChatActivity.this, UserHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        back.setOnClickListener(v ->{
            Intent intent = new Intent(UserDoctorChatActivity.this, UserHomeActivity.class);
            startActivity(intent);
            finish();
        });
        doctorName.setText(doctor.getName());
        sendMessage.setOnClickListener(v ->{
            String message = typeMessage.getEditText().getText().toString();
            if(!message.isEmpty()){
                if(NetworkService.isConnected(getApplicationContext())){
                    try {
                        sendMessageToDB(message);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    NetworkService.connectionFailed(getApplicationContext());
                }
            }
        });
        String json = getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        user = gson.fromJson(json,UserModel.class);
        adapter = new UserDoctorMessageRecyclerAdapter(user);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
    }
    private void sendMessageToDB(String message) throws Exception {
        MessageModel messageModel = new MessageModel(doctor.getName(),user.getName(),message,doctor.getId(),user.getId(),doctor.getEmail(),user.getEmail(),doctor.getProfileImgUri(),user.getProfileImgUri(),user.getId(),doctor.getId());
        String ref = doctor.getId();
        String data = security.encrypt(messageModel);
        FirebaseService.getFirebaseDatabase().getReference("Message-User").child(user.getId())
                .child(doctor.getId())
                .push()
                .setValue(data)
                .addOnSuccessListener(x ->{
                    FirebaseService.getFirebaseDatabase().getReference("Message-Doctor").child(doctor.getId())
                            .child(user.getId())
                            .push()
                            .setValue(data)
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
                            MessageModel message = null;
                            try {
                                message = security.decryptMessage(snapshot1.getValue().toString());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
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