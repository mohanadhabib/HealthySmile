package com.buc.gradution.View.Activity.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.Adapter.Doctor.DoctorUserMessageRecyclerAdapter;
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

public class DoctorChatActivity extends AppCompatActivity {
    private DoctorModel doctor;
    private UserModel user;
    private ImageView back,phone,video;
    private TextView patientName,noData;
    private RecyclerView recyclerView;
    private TextInputLayout typeMessage;
    private MaterialButton sendMessage;
    private DoctorUserMessageRecyclerAdapter adapter;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_chat);
        initComponents();
        String json = getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        user = (UserModel) getIntent().getSerializableExtra(Constant.OBJECT);
        doctor = gson.fromJson(json,DoctorModel.class);
        back.setOnClickListener(v -> {
            finish();
        });
        patientName.setText(user.getName());
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
        adapter = new DoctorUserMessageRecyclerAdapter(doctor);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        getMessageFirstTime(user.getId());
    }
    private void sendMessageToDB(String message){
        MessageModel messageModel = new MessageModel(doctor.getName(),user.getName(),message,user.getId(),doctor.getId(),doctor.getEmail(),user.getEmail(),doctor.getProfileImgUri(),user.getProfileImgUri(),user.getId(),doctor.getId());
        String ref = user.getId();
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
        FirebaseService.getFirebaseDatabase().getReference("Message-Doctor").child(doctor.getId())
                .child(ref)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
        FirebaseService.getFirebaseDatabase().getReference("Message-Doctor").child(doctor.getId())
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
        patientName = findViewById(R.id.patient_name);
        video = findViewById(R.id.video_call);
        phone = findViewById(R.id.phone_call);
        noData = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.messages_recycler);
        typeMessage = findViewById(R.id.type_message);
        sendMessage = findViewById(R.id.send_message);
    }
}