package com.buc.gradution.View.Fragment.User;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Service.FirebaseSecurity;
import com.buc.gradution.View.Activity.User.UserAllDoctorActivity;
import com.buc.gradution.View.Activity.User.UserDoctorSearchActivity;
import com.buc.gradution.Adapter.User.TopDoctorsRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserHomeFragment extends Fragment {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView notification;
    private TextInputLayout doctorSearch;
    private MaterialButton btn;
    private TextView seeAll,noItem;
    private RecyclerView recyclerView;
    private TopDoctorsRecyclerAdapter adapter;
    private ArrayList<DoctorModel> doctors;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_home,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        context = view.getContext();
        btn.setOnClickListener(v -> {
            String url = "https://sites.google.com/view/healthy-smile-tech-p/home";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
        seeAll.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), UserAllDoctorActivity.class);
            intent.putExtra("doctors",doctors);
            startActivity(intent);
        });
        doctorSearch.getEditText().setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserDoctorSearchActivity.class);
            intent.putExtra("doctors",doctors);
            startActivity(intent);
        });
        recyclerView.setVisibility(View.INVISIBLE);
        noItem.setVisibility(View.VISIBLE);
        new NetworkService().getNetworkState(context,new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                handler.post(()-> getDoctors());
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
    private void initComponents(View view){
        notification = view.findViewById(R.id.notification);
        doctorSearch = view.findViewById(R.id.search_doctor);
        noItem = view.findViewById(R.id.no_item_txt);
        btn = view.findViewById(R.id.btn);
        seeAll = view.findViewById(R.id.see_more_doctors);
        recyclerView = view.findViewById(R.id.top_doctors_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
    }
    private void getDoctors(){
        FirebaseService.getFirebaseDatabase().getReference(Constant.DOCTOR_TYPE)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                doctors = new ArrayList<>();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                    DoctorModel doctor;
                                    try {
                                        doctor = security.decryptDoctor(snapshot1.getValue().toString());
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    doctors.add(doctor);
                                }
                                if(doctors.isEmpty()){
                                    noItem.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.INVISIBLE);
                                }else {
                                    noItem.setVisibility(View.INVISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                                adapter = new TopDoctorsRecyclerAdapter();
                                adapter.setDoctors(doctors);
                                recyclerView.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        }
                );
    }
}
