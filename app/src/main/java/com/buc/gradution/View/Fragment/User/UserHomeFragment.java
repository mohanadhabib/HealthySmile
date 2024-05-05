package com.buc.gradution.View.Fragment.User;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Adapter.User.TopDoctorsRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseSecurity;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.buc.gradution.View.Activity.User.UserAllDoctorActivity;
import com.buc.gradution.View.Activity.User.UserDoctorSearchActivity;
import com.buc.gradution.View.Activity.User.UserGuideActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class UserHomeFragment extends Fragment {
    private String[] languages;
    private final FirebaseSecurity security = new FirebaseSecurity();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView userGuide,menu;
    private TextInputLayout doctorSearch;
    private MaterialButton btn;
    private TextView seeAll,noItem;
    private RecyclerView recyclerView;
    private TopDoctorsRecyclerAdapter adapter;
    private ArrayList<DoctorModel> doctors;
    private Boolean isDarkTheme;
    private SharedPreferences.Editor themeEditor;
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
        languages = new String[]{getResources().getString(R.string.english),getResources().getString(R.string.arabic)};
        context = view.getContext();
        isDarkTheme = getActivity().getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).getBoolean(Constant.CURRENT_THEME,false);
        menu.setOnClickListener(v -> settingsPopUp());
        userGuide.setOnClickListener(v ->{
            if(NetworkService.isConnected(getActivity().getApplicationContext())){
                boolean isEnglish = Locale.getDefault().getLanguage().contentEquals("en");
                Intent intent = new Intent(getActivity().getApplicationContext(), UserGuideActivity.class);
                if(isEnglish){
                    intent.putExtra("url","https://drive.google.com/file/d/117O8OjdD4kAtRgl9EKE1ydzNKdQE49_b/view?usp=drivesdk");
                }
                else {
                    intent.putExtra("url","https://drive.google.com/file/d/15y9mimifNeyOhWzold7kcXYSGb533s0E/view?usp=drivesdk");
                }
                startActivity(intent);
            }
            else{
                NetworkService.connectionFailed(getActivity().getApplicationContext());
            }
        });
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
        userGuide = view.findViewById(R.id.user_guide);
        doctorSearch = view.findViewById(R.id.search_doctor);
        noItem = view.findViewById(R.id.no_item_txt);
        btn = view.findViewById(R.id.btn);
        seeAll = view.findViewById(R.id.see_more_doctors);
        recyclerView = view.findViewById(R.id.top_doctors_recycler);
        menu = view.findViewById(R.id.menu);
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
