package com.buc.gradution.View.Fragment.Doctor;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.buc.gradution.View.Activity.OnboardingFourActivity;
import com.buc.gradution.View.Activity.AiChatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DoctorProfileFragment extends Fragment {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ShapeableImageView profileImg;
    private TextView userName;
    private LinearLayout savedLayout,appointmentLayout, geminiChatLayout,faqLayout,logOutLayout;
    private Context context;
    private DoctorModel doctor;
    public DoctorProfileFragment (ViewPager2 viewPager, BottomNavigationView bottomNavigationView){
        this.viewPager = viewPager;
        this.bottomNavigationView = bottomNavigationView;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_profile,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        getUserInfo();
        context = view.getContext();
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result->{
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
        appointmentLayout.setOnClickListener(v ->{
            viewPager.setCurrentItem(0,false);
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
            boolean isEnglish = Locale.getDefault().getLanguage().contentEquals("en");
            if(isEnglish){
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse("https://downgit.github.io/#/home?url=https://github.com/mohanadhabib/HealthySmile/blob/master/Healthy%20Smile%20User%20Guide%20-%20English.pdf");
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                manager.enqueue(request);
            }
        });
        geminiChatLayout.setOnClickListener(v ->{
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
    }
    private void getUserInfo(){
        Gson gson = new Gson();
        String json = getActivity().getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
        doctor = gson.fromJson(json, DoctorModel.class);
        Picasso.get().load(doctor.getProfileImgUri()).into(profileImg);
        userName.setText(doctor.getName());
    }
    private void updateImage(Uri uri){
        FirebaseService.getFirebaseStorage().getReference(doctor.getId()).child("profile image")
                .putFile(uri)
                .addOnSuccessListener(v -> updateUser())
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void updateUser(){
        FirebaseService.getFirebaseStorage()
                .getReference(doctor.getId())
                .child("profile image")
                .getDownloadUrl()
                .addOnSuccessListener(uri ->{
                    doctor.setProfileImgUri(uri.toString());
                    FirebaseService.getFirebaseDatabase().getReference(doctor.getType()).child(doctor.getId()).setValue(doctor);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}