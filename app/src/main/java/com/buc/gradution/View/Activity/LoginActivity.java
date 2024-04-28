package com.buc.gradution.View.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Context;
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

import com.buc.gradution.Service.FirebaseSecurity;
import com.buc.gradution.View.Activity.Doctor.DoctorHomeActivity;
import com.buc.gradution.View.Activity.User.UserGuideActivity;
import com.buc.gradution.View.Activity.User.UserHomeActivity;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {

    private final FirebaseSecurity security = new FirebaseSecurity();
    private String emailTxt, passwordTxt;
    private ImageView userGuide;
    private TextView loading;
    private MaterialSwitch themeSwitch;
    private ImageView back;
    private TextInputLayout email , password;
    private MaterialCheckBox doctorCheckBox;
    private TextView forgetPasswordBtn, signupBtn , emailHint , passwordHint ;
    private MaterialButton loginBtn , googleLoginBtn, facebookLoginBtn;
    private CircularProgressIndicator progressIndicator;
    private Boolean isDarkTheme;
    private SharedPreferences.Editor themeEditor;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        getDataFromSignUp();
        context = getApplicationContext();
        isDarkTheme = getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).getBoolean(Constant.CURRENT_THEME,false);
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if(o.getResultCode() == RESULT_OK){
                try {
                    firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(o.getData()).getResult().getIdToken());
                }catch (Throwable e){
                    Toast.makeText(getApplicationContext(),"Couldn't Sign in",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (isDarkTheme){
            themeSwitch.setThumbIconDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_dark_mode));
        }else{
            themeSwitch.setThumbIconDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_light_mode));
        }
        themeSwitch.setChecked(isDarkTheme);
        userGuide.setOnClickListener(v ->{
            if(NetworkService.isConnected(getApplicationContext())){
                boolean isEnglish = Locale.getDefault().getLanguage().contentEquals("en");
                Intent intent = new Intent(getApplicationContext(), UserGuideActivity.class);
                if(isEnglish){
                    intent.putExtra("url","https://drive.google.com/file/d/117O8OjdD4kAtRgl9EKE1ydzNKdQE49_b/view?usp=drivesdk");
                }
                else {
                    intent.putExtra("url","https://drive.google.com/file/d/15y9mimifNeyOhWzold7kcXYSGb533s0E/view?usp=drivesdk");
                }
                startActivity(intent);
            }
            else{
                NetworkService.connectionFailed(getApplicationContext());
            }
        });
        themeSwitch.setOnClickListener(v -> {
            themeEditor = getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).edit();
            isDarkTheme = !isDarkTheme;
            themeEditor.putBoolean(Constant.CURRENT_THEME,isDarkTheme);
            themeEditor.commit();
            themeSwitch.setChecked(isDarkTheme);
            if(isDarkTheme){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        back.setOnClickListener(v -> finish());
        password.setEndIconOnClickListener(v -> showAndHidePassword(password));
        loginBtn.setOnClickListener(v -> {
            progressIndicator.setProgress(0);
            boolean isEmailValid = emailValidation(email);
            boolean isPasswordValid = passwordValidation(password);
            if (isEmailValid && isPasswordValid) {
                if (NetworkService.isConnected(context)) {
                    String emailTxt = email.getEditText().getText().toString();
                    String passwordTxt = password.getEditText().getText().toString();
                    login(emailTxt, passwordTxt);
                } else {
                    NetworkService.connectionFailed(context);
                }
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
        googleLoginBtn.setOnClickListener(v -> launcher.launch(googleSignIn()));
    }
    private void initComponents(){
        loading = findViewById(R.id.loading_txt);
        userGuide = findViewById(R.id.user_guide);
        themeSwitch = findViewById(R.id.theme);
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
        emailHint = findViewById(R.id.email_hint);
        passwordHint = findViewById(R.id.password_hint);
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
            email.setErrorEnabled(true);
            emailHint.setVisibility(View.INVISIBLE);
            email.setError(getString(R.string.email_is_mandatory));
        }
        else if(!email.getEditText().getText().toString().contains("@")){
            email.setErrorEnabled(true);
            emailHint.setVisibility(View.INVISIBLE);
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
                    email.setErrorEnabled(false);
                    emailHint.setVisibility(View.VISIBLE);
                    email.setError(null);
                }
                else if(s.toString().contains("@")){
                    email.setErrorEnabled(false);
                    emailHint.setVisibility(View.VISIBLE);
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
            password.setErrorEnabled(true);
            passwordHint.setVisibility(View.INVISIBLE);
            password.setError(getString(R.string.password_is_mandatory));
        }
        else if(password.getEditText().getText().toString().length() < 8){
            password.setErrorEnabled(true);
            passwordHint.setVisibility(View.INVISIBLE);
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
                    password.setErrorEnabled(false);
                    passwordHint.setVisibility(View.VISIBLE);
                    password.setError(null);
                }
                else if(s.toString().length() > 8){
                    password.setErrorEnabled(false);
                    passwordHint.setVisibility(View.VISIBLE);
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
        loginBtn.setClickable(false);
        loading.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);
        progressIndicator.setProgress(50,true);
        FirebaseService.getFirebaseAuth()
                .signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    loginBtn.setClickable(true);
                    searchForUser(authResult);
                }).addOnFailureListener(e -> {
                    loginBtn.setClickable(true);
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    loading.setVisibility(View.INVISIBLE);
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
                                UserModel user;
                                try {
                                    user = security.decryptUser(data0.getValue().toString());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                if(authResult.getUser().getUid().equals(user.getId())){
                                    writeToSharedPreferences(Constant.PATIENT_TYPE,user);
                                    isRightType.set(true);
                                    homePagePopUp(UserHomeActivity.class);
                                }
                            }
                            if(!isRightType.get()){
                                Toast.makeText(LoginActivity.this, "Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
            }
            else if(type.equals(Constant.DOCTOR_TYPE)){
                FirebaseService.getFirebaseDatabase().getReference(Constant.DOCTOR_TYPE).get()
                        .addOnSuccessListener(dataSnapshot -> {
                            for(DataSnapshot data0 : dataSnapshot.getChildren()){
                                DoctorModel user;
                                try {
                                    user = security.decryptDoctor(data0.getValue().toString());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                if(authResult.getUser().getUid().equals(user.getId())){
                                    writeToSharedPreferences(Constant.DOCTOR_TYPE,user);
                                    isRightType.set(true);
                                    homePagePopUp(DoctorHomeActivity.class);
                                }
                            }
                            if(!isRightType.get()){
                                Toast.makeText(LoginActivity.this, "Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
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
    private void writeToSharedPreferences(String type,UserModel userModel){
        Gson gson = new Gson();
        SharedPreferences.Editor sharedPreference = getSharedPreferences(Constant.USER_TYPE,0).edit();
        SharedPreferences.Editor currentUser = getSharedPreferences(Constant.CURRENT_USER,0).edit();
        String user = gson.toJson(userModel);
        currentUser.putString(Constant.OBJECT,user);
        currentUser.commit();
        if(type.equals(Constant.DOCTOR_TYPE)){
            sharedPreference.putString(Constant.TYPE,Constant.DOCTOR_TYPE);
            sharedPreference.commit();
        }
        else if(type.equals(Constant.PATIENT_TYPE)){
            sharedPreference.putString(Constant.TYPE,Constant.PATIENT_TYPE);
            sharedPreference.commit();
        }
    }
    private Intent googleSignIn(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constant.webId)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        return googleSignInClient.getSignInIntent();
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        FirebaseService.getFirebaseAuth()
                .signInWithCredential(credential)
                .addOnSuccessListener(v ->{
                    FirebaseUser user = FirebaseService.getFirebaseAuth().getCurrentUser();
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    try {
                        registerUserWithGoogle(user,v);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .addOnFailureListener(e ->{
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    loading.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this,"Sorry, Couldn't login\nPlease try again", Toast.LENGTH_SHORT).show();
                });
        }
    private void registerUserWithGoogle(FirebaseUser firebaseUser,AuthResult authResult) throws Exception {
        String type = getUserType(doctorCheckBox);
        UserModel user = new UserModel();
        DoctorModel doctor =  new DoctorModel();
        String data = "";
        if(type.equals(Constant.PATIENT_TYPE)){
            user = new UserModel(firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getEmail(),type,firebaseUser.getPhotoUrl().toString(),firebaseUser.getPhoneNumber());
            data = security.encrypt(user);
        }else if (type.equals(Constant.DOCTOR_TYPE)){
            doctor = new DoctorModel(firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getEmail(),type,firebaseUser.getPhotoUrl().toString(),firebaseUser.getPhoneNumber(),"Dentist","4,5","750m","");
            data = security.encrypt(doctor);
        }
        FirebaseService.getFirebaseDatabase()
                .getReference(getUserType(doctorCheckBox))
                .child(user.getId())
                .setValue(data)
                .addOnSuccessListener(v -> searchForUser(authResult))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show());
    }
}