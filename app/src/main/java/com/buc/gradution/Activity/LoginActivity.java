package com.buc.gradution.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.Activity.Doctor.DoctorHomeActivity;
import com.buc.gradution.Activity.User.UserHomeActivity;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {

    private String emailTxt, passwordTxt;
    private ImageView back;
    private TextInputLayout email , password;
    private MaterialCheckBox doctorCheckBox;
    private TextView forgetPasswordBtn, signupBtn;
    private MaterialButton loginBtn , googleLoginBtn, facebookLoginBtn;
    private CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        getDataFromSignUp();
        back.setOnClickListener(v -> finish());
        password.setEndIconOnClickListener(v -> showAndHidePassword(password));
        loginBtn.setOnClickListener(v -> {
            boolean isEmailValid = emailValidation(email);
            boolean isPasswordValid = passwordValidation(password);
            if (isEmailValid && isPasswordValid) {
                String emailTxt = email.getEditText().getText().toString();
                String passwordTxt = password.getEditText().getText().toString();
                login(emailTxt,passwordTxt);
            }
        });
        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
        forgetPasswordBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }
    private void initComponents(){
        back = findViewById(R.id.back_button);
        email = findViewById(R.id.email_layout);
        password = findViewById(R.id.password_layout);
        doctorCheckBox = findViewById(R.id.doctor);
        forgetPasswordBtn = findViewById(R.id.forget_password);
        loginBtn = findViewById(R.id.login_button);
        signupBtn = findViewById(R.id.signUp_button);
        googleLoginBtn = findViewById(R.id.google_sign_in);
        facebookLoginBtn = findViewById(R.id.facebook_sign_in);
        progressIndicator = findViewById(R.id.progress);
    }
    private void getDataFromSignUp(){
        if(getIntent().getExtras() != null){
            emailTxt = getIntent().getExtras().getString("email");
            passwordTxt = getIntent().getExtras().getString("password");
        }
        if(emailTxt == null && passwordTxt == null){
            email.getEditText().setText(null);
            password.getEditText().setText(null);
        }
        else {
            email.getEditText().setText(emailTxt);
            password.getEditText().setText(passwordTxt);
        }
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
    private void login(String email , String password){
        progressIndicator.setVisibility(View.VISIBLE);
        progressIndicator.setProgress(50,true);
        FirebaseService.getFirebaseAuth()
                .signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    searchForUser(authResult);
                }).addOnFailureListener(e -> {
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(LoginActivity.this,"Sorry, Couldn't login\nPlease try again", Toast.LENGTH_SHORT).show();
                });
    }
    public void homePagePopUp(Class c){
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View view = inflater.inflate(R.layout.alert_dialog_login,null);
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this).create();
        MaterialButton goToHome = view.findViewById(R.id.go_to_home);
        alertDialog.setView(view);
        alertDialog.show();
        goToHome.setOnClickListener(v ->{
            Intent intent = new Intent(LoginActivity.this,c);
            startActivity(intent);
            finish();
        });
    }
    private void searchForUser(AuthResult authResult){
            String type = getUserType(doctorCheckBox);
            AtomicBoolean isRightType = new AtomicBoolean(false);
            if(type.equals(Constant.PATIENT_TYPE)){
                FirebaseService.getFirebaseDatabase().getReference(Constant.PATIENT_TYPE).get()
                        .addOnSuccessListener(dataSnapshot -> {
                            for(DataSnapshot data0 : dataSnapshot.getChildren()){
                                UserModel user = data0.getValue(UserModel.class);
                                if(authResult.getUser().getUid().equals(user.getId())){
                                    writeToSharedPreferences(Constant.PATIENT_TYPE);
                                    isRightType.set(true);
                                    homePagePopUp(UserHomeActivity.class);
                                }
                            }
                            if(!isRightType.get()){
                                Toast.makeText(LoginActivity.this, "Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
            else if(type.equals(Constant.DOCTOR_TYPE)){
                FirebaseService.getFirebaseDatabase().getReference(Constant.DOCTOR_TYPE).get()
                        .addOnSuccessListener(dataSnapshot -> {
                            for(DataSnapshot data0 : dataSnapshot.getChildren()){
                                UserModel user = data0.getValue(UserModel.class);
                                if(authResult.getUser().getUid().equals(user.getId())){
                                    writeToSharedPreferences(Constant.DOCTOR_TYPE);
                                    isRightType.set(true);
                                    homePagePopUp(DoctorHomeActivity.class);
                                }
                            }
                            if(!isRightType.get()){
                                Toast.makeText(LoginActivity.this, "Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

    }
    private String getUserType(MaterialCheckBox checkBox){
        if(checkBox.isChecked()){
            return Constant.DOCTOR_TYPE;
        }
        else{
            return Constant.PATIENT_TYPE;
        }
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