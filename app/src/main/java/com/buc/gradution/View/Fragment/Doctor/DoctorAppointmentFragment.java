package com.buc.gradution.View.Fragment.Doctor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.buc.gradution.Adapter.Doctor.DoctorAppointmentsRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.AppointmentModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseSecurity;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorAppointmentFragment extends Fragment {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView noAppointmentText;
    private RecyclerView recyclerView;
    private DoctorAppointmentsRecyclerAdapter adapter;
    private View root;
    private ArrayList<AppointmentModel> appointments;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_appointment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        root = view;
        context = getContext();
        new NetworkService().getNetworkState(context,new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                handler.post(()-> getAppointmentsFromDB());
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                handler.post(()-> NetworkService.connectionFailed(context));
            }
        });
    }
    private void getAppointmentsFromDB(){
        FirebaseService.getFirebaseDatabase().getReference(Constant.APPOINTMENT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AppointmentModel appointment;
                        appointments = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            try {
                                appointment = security.decryptAppointment(dataSnapshot.getValue().toString());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            if(appointment.getDoctorId().equals(FirebaseService.getFirebaseAuth().getCurrentUser().getUid())){
                                noAppointmentText.setVisibility(View.INVISIBLE);
                                appointments.add(appointment);
                            }
                        }
                        adapter.setAppointments(appointments);
                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
    private void initComponents(View view){
        noAppointmentText = view.findViewById(R.id.no_appointment_text);
        recyclerView = view.findViewById(R.id.recycler);
        adapter = new DoctorAppointmentsRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
}