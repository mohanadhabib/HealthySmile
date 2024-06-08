package com.buc.gradution.view.fragment.doctor;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.buc.gradution.adapter.doctor.DoctorAppointmentsRecyclerAdapter;
import com.buc.gradution.constant.Constant;
import com.buc.gradution.model.AppointmentModel;
import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseSecurity;
import com.buc.gradution.service.FirebaseService;
import com.buc.gradution.service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorAppointmentFragment extends Fragment {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView menu;
    private TextView noAppointmentText;
    private RecyclerView recyclerView;
    private DoctorAppointmentsRecyclerAdapter adapter;
    private View root;
    private ArrayList<AppointmentModel> appointments;
    private SharedPreferences.Editor themeEditor;
    private Context context;
    private Boolean isDarkTheme;
    private String[] languages;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_appointment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        languages = new String[]{getResources().getString(R.string.english),getResources().getString(R.string.arabic)};
        isDarkTheme = getActivity().getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).getBoolean(Constant.CURRENT_THEME,false);
        root = view;
        context = getContext();
        menu.setOnClickListener(v -> settingsPopUp());
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
        menu = view.findViewById(R.id.menu);
        recyclerView = view.findViewById(R.id.recycler);
        adapter = new DoctorAppointmentsRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
    private void settingsPopUp(){
        LayoutInflater inflater = LayoutInflater.from(this.getActivity());
        View view = inflater.inflate(R.layout.alert_home_menu,null);
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(this.getActivity()).create();
        alertDialog.setView(view);
        alertDialog.show();
        MaterialSwitch theme = view.findViewById(R.id.theme);
        MaterialButton close = view.findViewById(R.id.close);
        TextInputLayout language = view.findViewById(R.id.language_layout);
        MaterialAutoCompleteTextView autoCompleteTextView = (MaterialAutoCompleteTextView) language.getEditText();
        if (isDarkTheme){
            theme.setThumbIconDrawable(AppCompatResources.getDrawable(getActivity().getApplicationContext(),R.drawable.ic_dark_mode));
        }else{
            theme.setThumbIconDrawable(AppCompatResources.getDrawable(getActivity().getApplicationContext(),R.drawable.ic_light_mode));
        }
        theme.setChecked(isDarkTheme);
        theme.setOnClickListener(v -> {
            themeEditor = getActivity().getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).edit();
            isDarkTheme = !isDarkTheme;
            themeEditor.putBoolean(Constant.CURRENT_THEME,isDarkTheme);
            themeEditor.commit();
            theme.setChecked(isDarkTheme);
            if(isDarkTheme){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                alertDialog.dismiss();
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                alertDialog.dismiss();
            }
        });
        autoCompleteTextView.setSimpleItems(languages);
        autoCompleteTextView.dismissDropDown();
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            if(position == 0){
                LocaleListCompat locale = LocaleListCompat.forLanguageTags("en");
                AppCompatDelegate.setApplicationLocales(locale);
                alertDialog.dismiss();
            }
            else if(position == 1){
                LocaleListCompat locale = LocaleListCompat.forLanguageTags("ar");
                AppCompatDelegate.setApplicationLocales(locale);
                alertDialog.dismiss();
            }
        });
        close.setOnClickListener(v -> alertDialog.dismiss());
    }
}