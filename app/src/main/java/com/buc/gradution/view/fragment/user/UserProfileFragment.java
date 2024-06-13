package com.buc.gradution.view.fragment.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.buc.gradution.constant.Constant;
import com.buc.gradution.model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseSecurity;
import com.buc.gradution.service.FirebaseService;
import com.buc.gradution.service.NetworkService;
import com.buc.gradution.view.activity.AiChatActivity;
import com.buc.gradution.view.activity.GeminiChatActivity;
import com.buc.gradution.view.activity.OnboardingFourActivity;
import com.buc.gradution.view.activity.user.UserGuideActivity;
import com.buc.gradution.view.activity.user.UserHistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class UserProfileFragment extends Fragment {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private final ViewPager2 viewPager;
    private final BottomNavigationView bottomNavigationView;
    private ShapeableImageView profileImg;
    private TextView userName;
    private LinearLayout savedLayout,appointmentLayout,geminiChatLayout,faqLayout,logOutLayout,chatBotLayout;
    private SharedPreferences.Editor editor;
    private Context context;
    private UserModel user;
    public UserProfileFragment (ViewPager2 viewPager, BottomNavigationView bottomNavigationView){
        this.viewPager = viewPager;
        this.bottomNavigationView = bottomNavigationView;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        getUserInfo();
        context = view.getContext();
        editor = context.getSharedPreferences(Constant.LANGUAGE_SHARED_PREFERENCES,0).edit();
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
            if(result.getResultCode() == getActivity().RESULT_OK && result.getData() != null){
                updateImage(result.getData().getData());
                profileImg.setImageURI(result.getData().getData());
            }else{
                Toast.makeText(getContext(), "Please select an image ", Toast.LENGTH_SHORT).show();
            }
        });
        profileImg.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
        savedLayout.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity().getApplicationContext(), UserHistoryActivity.class);
            startActivity(intent);
        });
        appointmentLayout.setOnClickListener(v ->{
            viewPager.setCurrentItem(3,false);
            bottomNavigationView.setSelectedItemId(R.id.appointment);
        });
        logOutLayout.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View myView = inflater.inflate(R.layout.alert_dialog_logout,null);
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext()).create();
            MaterialButton logout = myView.findViewById(R.id.logout_button);
            TextView cancel = myView.findViewById(R.id.cancel_button);
            alertDialog.setView(myView);
            alertDialog.show();
            logout.setOnClickListener(a ->{
                if(NetworkService.isConnected(context)){
                    FirebaseService.getFirebaseAuth().signOut();
                    getActivity().getSharedPreferences(Constant.CURRENT_USER,0).edit().putString(Constant.OBJECT," ").commit();
                    getActivity().getSharedPreferences(Constant.USER_TYPE,0).edit().putString(Constant.TYPE," ").commit();
                    Intent intent = new Intent(getContext(), OnboardingFourActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }else{
                    NetworkService.connectionFailed(context);
                }
            });
            cancel.setOnClickListener(b -> alertDialog.cancel());
        });
        faqLayout.setOnClickListener(v -> {
            if(NetworkService.isConnected(context)){
                boolean isEnglish = Locale.getDefault().getLanguage().contentEquals("en");
                Intent intent = new Intent(context, UserGuideActivity.class);
                if(isEnglish){
                    intent.putExtra("url","https://drive.google.com/file/d/117O8OjdD4kAtRgl9EKE1ydzNKdQE49_b/view?usp=drivesdk");
                }
                else {
                    intent.putExtra("url","https://drive.google.com/file/d/15y9mimifNeyOhWzold7kcXYSGb533s0E/view?usp=drivesdk");
                }
                startActivity(intent);
            }
            else{
                NetworkService.connectionFailed(context);
            }
        });
        geminiChatLayout.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity().getApplicationContext(), GeminiChatActivity.class);
            startActivity(intent);
        });
        chatBotLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), AiChatActivity.class);
            startActivity(intent);
        });
    }
    private void initComponents(View view){
        profileImg = view.findViewById(R.id.profile_img);
        userName = view.findViewById(R.id.user_name);
        savedLayout = view.findViewById(R.id.layout_saved);
        appointmentLayout = view.findViewById(R.id.layout_appointment);
        geminiChatLayout = view.findViewById(R.id.layout_gemini);
        faqLayout = view.findViewById(R.id.layout_faq);
        logOutLayout = view.findViewById(R.id.layout_logout);
        chatBotLayout = view.findViewById(R.id.layout_chat_bot);
    }
    private void getUserInfo(){
        Gson gson = new Gson();
        String json = getActivity().getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        user = gson.fromJson(json, UserModel.class);
        Picasso.get().load(user.getProfileImgUri()).into(profileImg);
        userName.setText(user.getName());
    }
    private void updateImage(Uri uri){
        FirebaseService.getFirebaseStorage().getReference(user.getId()).child("profile image")
                .putFile(uri)
                .addOnSuccessListener(v -> updateUser())
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void updateUser(){
        FirebaseService.getFirebaseStorage()
                .getReference(user.getId())
                .child("profile image")
                .getDownloadUrl()
                .addOnSuccessListener(uri ->{
                    user.setProfileImgUri(uri.toString());
                    String data;
                    try {
                         data = security.encrypt(user);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    FirebaseService.getFirebaseDatabase().getReference(user.getType()).child(user.getId()).setValue(data);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}