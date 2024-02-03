package com.buc.gradution.Fragment.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Activity.User.UserDoctorDetailsActivity;
import com.buc.gradution.Adapter.UserAppointmentsRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.AppointmentModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.AppointmentInterface;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserAppointmentFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAppointmentsRecyclerAdapter adapter;
    private View root;
    private ArrayList<AppointmentModel> appointments;
    private Context context;
    private Gson gson = new Gson();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_appointment,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        root = view;
        context = getContext();
        updateView(0);
        new NetworkService().getNetworkState(context,new ConnectivityManager.NetworkCallback(){
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
        this.getActivity().runOnUiThread(() ->{
            switch (op){
                case 0 :
                    getAppointmentFromStorage();
                    break;
                case 1:
                    getAppointmentsFromDB();
                    break;
            }
        });
    }
    private void getAppointmentFromStorage(){
        String json = context.getSharedPreferences(Constant.APPOINTMENT_LIST,0).getString(Constant.APPOINTMENTS,"");
        if(json == ""){
            appointments = new ArrayList<>();
        }else{
            appointments = gson.fromJson(json,new TypeToken<ArrayList<AppointmentModel>>(){}.getType());
        }
        adapter = new UserAppointmentsRecyclerAdapter();
        adapter.setAppointments(appointments);
        recyclerView.setAdapter(adapter);
    }
    private void getAppointmentsFromDB(){
        FirebaseService.getFirebaseDatabase().getReference(Constant.APPOINTMENT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AppointmentModel appointment;
                        appointments = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            appointment = dataSnapshot.getValue(AppointmentModel.class);
                            appointments.add(appointment);
                        }
                        adapter = new UserAppointmentsRecyclerAdapter(new AppointmentInterface() {
                            @Override
                            public void onDelete(AppointmentModel appointment) {
                                deleteFromDB(appointment);
                            }
                            @Override
                            public void onReschedule(AppointmentModel appointment) {
                                rescheduleFromDB(appointment);
                            }
                        });
                        adapter.setAppointments(appointments);
                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
    private void rescheduleFromDB(AppointmentModel appointment){
        Intent intent = new Intent(context, UserDoctorDetailsActivity.class);
        intent.putExtra(Constant.APPOINTMENT,appointment);
        startActivity(intent);
    }
    private void deleteFromDB(AppointmentModel appointment){
        String ref = appointment.getUserId() + appointment.getDoctorId();
        appointments = new ArrayList<>();
        FirebaseService.getFirebaseDatabase().getReference(Constant.APPOINTMENT)
                .child(ref)
                .removeValue((error, ref1) -> ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AppointmentModel appointment1;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            appointment1 = dataSnapshot.getValue(AppointmentModel.class);
                            appointments.add(appointment1);
                        }
                        updateDataInStorage(appointments);
                        adapter.setAppointments(appointments);
                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                }));
    }
    private void updateDataInStorage(ArrayList<AppointmentModel> newAppointments){
        SharedPreferences.Editor shared = root.getContext().getSharedPreferences(Constant.APPOINTMENT_LIST,0).edit();
        String appointmentsTxt = gson.toJson(newAppointments);
        shared.putString(Constant.APPOINTMENTS,appointmentsTxt);
        shared.commit();
    }
    private void initComponents(View view){
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
}
