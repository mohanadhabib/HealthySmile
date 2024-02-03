package com.buc.gradution.Fragment.User;

import android.content.Context;
import android.content.Intent;
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

import com.buc.gradution.Activity.OnboardingFourActivity;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {
    private ShapeableImageView profileImg;
    private TextView userName;
    private LinearLayout savedLayout,appointmentLayout,paymentLayout,faqLayout,logOutLayout;
    private Context context;
    private UserModel user;
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
    }
    private void initComponents(View view){
        profileImg = view.findViewById(R.id.profile_img);
        userName = view.findViewById(R.id.user_name);
        savedLayout = view.findViewById(R.id.layout_saved);
        appointmentLayout = view.findViewById(R.id.layout_appointment);
        paymentLayout = view.findViewById(R.id.layout_payment);
        faqLayout = view.findViewById(R.id.layout_faq);
        logOutLayout = view.findViewById(R.id.layout_logout);
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
                    FirebaseService.getFirebaseDatabase().getReference(user.getType()).child(user.getId()).setValue(user);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
