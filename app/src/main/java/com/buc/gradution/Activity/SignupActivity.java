package com.buc.gradution.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import java.util.concurrent.atomic.AtomicReference;

public class SignupActivity extends AppCompatActivity {
    private ImageView back;
    private ShapeableImageView profileImg;
    private TextInputLayout name , email ,password;
    private MaterialCheckBox checkBox,doctorCheck;
    private MaterialButton signupBtn;
    private CircularProgressIndicator progressIndicator;
    private AtomicReference<Uri> uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initComponents();
        uri = new AtomicReference<>();
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null){
                profileImg.setImageURI(result.getData().getData());
                profileImg.setImageTintMode(null);
                uri.set(result.getData().getData());
            }else{
                Toast.makeText(SignupActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(v -> finish());
        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
        password.setEndIconOnClickListener(v -> showAndHidePassword(password));
        signupBtn.setOnClickListener(v -> {
            boolean isNameValid = nameValidation(name);
            boolean isEmailValid = emailValidation(email);
            boolean isPasswordValid = passwordValidation(password);
            boolean isCheckBoxChecked = isCheckboxChecked(checkBox);
            boolean isProfileImageSelected = uri.get() == null ? false : true;
            if(isProfileImageSelected == false){
                Toast.makeText(SignupActivity.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
            if(isNameValid && isEmailValid && isPasswordValid && isCheckBoxChecked && isProfileImageSelected){
                progressIndicator.setProgress(0,true);
                String nameTxt = name.getEditText().getText().toString();
                String emailTxt = email.getEditText().getText().toString();
                String passwordTxt = password.getEditText().getText().toString();
                signup(nameTxt,emailTxt,passwordTxt);
            }
        });
    }
    private void initComponents(){
        back = findViewById(R.id.back_button);
        profileImg = findViewById(R.id.profile_img);
        name = findViewById(R.id.name_layout);
        email = findViewById(R.id.email_layout);
        password = findViewById(R.id.password_layout);
        doctorCheck = findViewById(R.id.doctor);
        checkBox = findViewById(R.id.checkbox);
        signupBtn = findViewById(R.id.signup_button);
        progressIndicator = findViewById(R.id.progress);
    }
    private boolean nameValidation(TextInputLayout name){
        boolean isValid = false;
        if(name.getEditText().getText().toString().isEmpty()){
            name.setError(getString(R.string.name_is_mandatory));
        }
        else if(name.getEditText().getText().toString().charAt(0) < 65 ||
                name.getEditText().getText().toString().charAt(0) > 122 ||
                (name.getEditText().getText().toString().charAt(0) > 90 && name.getEditText().getText().toString().charAt(0) < 97 )) {
            name.setError(getString(R.string.name_must_start_with_letter));
        }
        name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    name.setError(null);
                }
                else if((s.toString().charAt(0) >= 65 && s.toString().charAt(0) <= 90 ) ||
                        (s.toString().charAt(0) >= 97 && s.toString().charAt(0) <= 122 )){
                    name.setError(null);
                }
            }
        });
        if(name.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private boolean emailValidation(TextInputLayout email){
        boolean isValid = false;
        if(email.getEditText().getText().toString().isEmpty()){
            email.setError(getString(R.string.email_is_mandatory));
        }
        else if(!email.getEditText().getText().toString().contains("@")){
            email.setError(getString(R.string.wrong_email_format));
        }
        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    email.setError(null);
                }
                else if(s.toString().contains("@")){
                    email.setError(null);
                }
            }
        });
        if(email.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private boolean passwordValidation(TextInputLayout password){
        boolean isValid = false;
        if(password.getEditText().getText().toString().isEmpty()){
            password.setError(getString(R.string.password_is_mandatory));
        }
        else if(password.getEditText().getText().toString().length() < 8){
            password.setError(getString(R.string.weak_password_password_must_be_at_least_8_letters));
        }
        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    password.setError(null);
                }
                else if(s.toString().length() > 8){
                    password.setError(null);
                }
            }
        });
        if(password.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private boolean isCheckboxChecked(MaterialCheckBox checkBox){
        boolean isValid = checkBox.isChecked();
        if(!isValid){
            checkBox.setError(getString(R.string.please_check_this_first));
        }
        else{
            checkBox.setError(null);
        }
        return isValid;
    }
    private String getUserType(MaterialCheckBox checkBox){
        if(checkBox.isChecked()){
            return Constant.DOCTOR_TYPE;
        }
        else{
            return Constant.PATIENT_TYPE;
        }
    }
    private void showAndHidePassword(TextInputLayout password){
        int visible = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        int invisible = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        int position = password.getEditText().getText().toString().length();
        if(password.getEditText().getInputType() == invisible){
            password.getEditText().setInputType(visible);
            password.getEditText().setSelection(position);
        }
        else if (password.getEditText().getInputType() == visible){
            password.getEditText().setInputType(invisible);
            password.getEditText().setSelection(position);
        }
    }
    private void createUser(AuthResult authResult ,String name ,String email ,String password ,Uri uri){
        String id = authResult.getUser().getUid();
        UserModel user = new UserModel(id,name,email,getUserType(doctorCheck),uri.toString());
        FirebaseService.getFirebaseDatabase()
                .getReference(getUserType(doctorCheck))
                .child(id)
                .setValue(user)
                .addOnSuccessListener(
                        e -> {
                            progressIndicator.setProgress(100,true);
                            progressIndicator.setVisibility(View.INVISIBLE);
                            writeToSharedPreferences(getUserType(doctorCheck));
                            LayoutInflater layoutInflater = LayoutInflater.from(this);
                            View layout = layoutInflater.inflate(R.layout.alert_dialog_signup,null);
                            MaterialButton login = layout.findViewById(R.id.login_button);
                            AlertDialog dialog = new MaterialAlertDialogBuilder(SignupActivity.this).create();
                            login.setOnClickListener( v -> {
                                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                intent.putExtra("email",user.getEmail());
                                intent.putExtra("password",password);
                                startActivity(intent);
                                finish();
                            });
                            dialog.setView(layout);
                            dialog.show();
                        }
                )
                .addOnFailureListener(e -> {
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, "Couldn't save user info", Toast.LENGTH_SHORT).show();
                });
    }
    private void signup(String name , String email , String password){
        progressIndicator.setVisibility(View.VISIBLE);
        progressIndicator.setProgress(10,true);
        FirebaseService.getFirebaseAuth()
                .createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    progressIndicator.setProgress(30,true);
                    uploadProfileImg(authResult,name,email,password,uri.get());
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, "Sorry, Couldn't create account\nPlease try again", Toast.LENGTH_SHORT).show();
                });
    }
    private void uploadProfileImg(AuthResult authResult,String name,String email,String password,Uri imageUri){
        FirebaseService.getFirebaseStorage()
                .getReference(authResult.getUser().getUid())
                .child("profile image")
                .putFile(imageUri)
                .addOnSuccessListener(v -> {
                    progressIndicator.setProgress(50,true);
                    v.getStorage()
                            .getStorage()
                            .getReference(authResult.getUser().getUid())
                            .child("profile image")
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                progressIndicator.setProgress(70,true);
                                createUser(authResult,name,email,password,uri);
                            })
                            .addOnFailureListener(e -> {
                                progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                                progressIndicator.setProgress(100,true);
                                progressIndicator.setVisibility(View.INVISIBLE);
                                progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                                Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void writeToSharedPreferences(String type){
        SharedPreferences.Editor sharedPreference = getSharedPreferences(Constant.PREF_NAME,0).edit();
        if(type.equals(Constant.DOCTOR_TYPE)){
            sharedPreference.putString(Constant.TYPE,Constant.DOCTOR_TYPE);
            sharedPreference.commit();
        }
        else if(type.equals(Constant.PATIENT_TYPE)){
            sharedPreference.putString(Constant.TYPE,Constant.PATIENT_TYPE);
            sharedPreference.commit();
        }
    }
}