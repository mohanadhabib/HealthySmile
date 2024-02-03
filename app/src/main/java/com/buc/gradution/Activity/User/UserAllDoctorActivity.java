package com.buc.gradution.Activity.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buc.gradution.Adapter.AllDoctorsRecyclerAdapter;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.NetworkService;

import java.util.ArrayList;

public class UserAllDoctorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView back;
    private TextView noItem;
    private ArrayList<DoctorModel> doctors;
    private AllDoctorsRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_doctor);
        initComponents();
        Intent intent = getIntent();
        updateView(0);
        doctors = (ArrayList<DoctorModel>) intent.getSerializableExtra("doctors");
        adapter = new AllDoctorsRecyclerAdapter();
        adapter.setDoctors(doctors);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserAllDoctorActivity.this,LinearLayoutManager.VERTICAL,false));
        back.setOnClickListener(v -> finish());
        new NetworkService().getNetworkState(getApplicationContext(),new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                updateView(1);
            }
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                updateView(0);
            }
        });
    }
    private void updateView(int op){
        this.runOnUiThread(() ->{
            switch (op){
                case 0 :
                    recyclerView.setVisibility(View.INVISIBLE);
                    noItem.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    recyclerView.setVisibility(View.VISIBLE);
                    noItem.setVisibility(View.INVISIBLE);
                    break;
            }
        });
    }
    private void initComponents(){
        recyclerView = findViewById(R.id.all_doctors_recycler);
        back = findViewById(R.id.back);
        noItem = findViewById(R.id.no_item_txt);
    }
}