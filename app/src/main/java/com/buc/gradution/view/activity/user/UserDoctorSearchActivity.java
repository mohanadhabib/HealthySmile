package com.buc.gradution.view.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buc.gradution.adapter.user.AllDoctorsRecyclerAdapter;
import com.buc.gradution.model.DoctorModel;
import com.buc.gradution.R;
import com.buc.gradution.service.NetworkService;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class UserDoctorSearchActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView back;
    private TextInputLayout searchDoctor;
    private RecyclerView recyclerView;
    private TextView noItem;
    private AllDoctorsRecyclerAdapter adapter;
    private ArrayList<DoctorModel> doctors,searchedResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_doctor_search);
        initComponents();
        recyclerView.setVisibility(View.INVISIBLE);
        noItem.setVisibility(View.VISIBLE);
        back.setOnClickListener(v -> finish());
        searchDoctor.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
               if(NetworkService.isConnected(getApplicationContext())){
                   if(!s.toString().equals("")){
                       searchedResult = getSearch(s.toString());
                   }else {
                       searchedResult = new ArrayList<>();
                   }
                   adapter = new AllDoctorsRecyclerAdapter();
                   adapter.setDoctors(searchedResult);
                   recyclerView.setAdapter(adapter);
                   recyclerView.setLayoutManager(new LinearLayoutManager(UserDoctorSearchActivity.this,RecyclerView.VERTICAL,false));
               }
               else {
                   NetworkService.connectionFailed(getApplicationContext());
               }
            }
        });
        new NetworkService().getNetworkState(getApplicationContext(),new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                handler.post(()->{
                    Intent intent = getIntent();
                    doctors = (ArrayList<DoctorModel>) intent.getSerializableExtra("doctors");
                    recyclerView.setVisibility(View.VISIBLE);
                    noItem.setVisibility(View.INVISIBLE);
                });
            }
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                handler.post(()-> {
                    recyclerView.setVisibility(View.INVISIBLE);
                    noItem.setVisibility(View.VISIBLE);
                });
            }
        });
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        searchDoctor = findViewById(R.id.search_doctor);
        recyclerView = findViewById(R.id.search_doctors_recycler);
        noItem = findViewById(R.id.no_item_txt);
    }
    private ArrayList<DoctorModel> getSearch(String txt){
        ArrayList<DoctorModel> users = new ArrayList<>();
        for(DoctorModel doctorModel : doctors){
            if(doctorModel.getName().toLowerCase().contains(txt.toLowerCase())){
                users.add(doctorModel);
            }
        }
        return users;
    }
}
