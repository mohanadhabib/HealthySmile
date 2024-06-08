package com.buc.gradution.view.activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.constant.Constant;
import com.buc.gradution.model.DoctorModel;
import com.buc.gradution.model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseSecurity;
import com.buc.gradution.service.FirebaseService;
import com.buc.gradution.service.NetworkService;
import com.buc.gradution.view.activity.user.UserGuideActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.MultiFactorSession;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SignupActivity extends AppCompatActivity {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private ImageView userGuide;
    private MaterialSwitch themeSwitch;
    private ImageView back;
    private ShapeableImageView profileImg;
    private TextInputLayout name , email ,password ,phone ,code;
    private TextView nameHint , emailHint, phoneHint, passwordHint;
    private MaterialCheckBox checkBox,doctorCheck;
    private MaterialButton signupBtn;
    private TextView loading,termsOfService,privacyPolicy;
    private CircularProgressIndicator progressIndicator;
    private Boolean isDarkTheme;
    private SharedPreferences.Editor themeEditor;
    private AtomicReference<Uri> uri;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initComponents();
        context = getApplicationContext();
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.user)+'/'+getResources().getResourceTypeName(R.drawable.user)+'/'+getResources().getResourceEntryName(R.drawable.user));
        uri = new AtomicReference<>();
        uri.set(imageUri);
        isDarkTheme = getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).getBoolean(Constant.CURRENT_THEME,false);
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null){
                profileImg.setImageURI(result.getData().getData());
                profileImg.setImageTintMode(null);
                uri.set(result.getData().getData());
            }else{
                profileImg.setImageResource(R.drawable.user);
                uri.set(imageUri);
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
        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
        password.setEndIconOnClickListener(v -> showAndHidePassword(password));
        signupBtn.setOnClickListener(v -> {
            progressIndicator.setProgress(0);
            boolean isNameValid = nameValidation(name);
            boolean isEmailValid = emailValidation(email);
            boolean isPasswordValid = passwordValidation(password);
            boolean isCheckBoxChecked = isCheckboxChecked(checkBox);
            boolean isPhoneValid = phoneValidation(phone);
            if(isNameValid && isEmailValid && isPasswordValid && isPhoneValid && isCheckBoxChecked ){
                if(NetworkService.isConnected(context)){
                    String nameTxt = name.getEditText().getText().toString();
                    String sanitizedNameTxt = sanitizeName(nameTxt);
                    String emailTxt = email.getEditText().getText().toString();
                    String passwordTxt = password.getEditText().getText().toString();
                    String phoneTxt = code.getEditText().getText().toString() + phone.getEditText().getText().toString();
                    signup(sanitizedNameTxt,emailTxt,passwordTxt,phoneTxt);
                }
                else{
                    NetworkService.connectionFailed(context);
                }
            }
        });
        termsOfService.setOnClickListener(v -> {
            String url = "https://sites.google.com/view/healthy-smile-tech/more/terms-of-service";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
        privacyPolicy.setOnClickListener(v ->{
            String url = "https://sites.google.com/view/healthy-smile-tech/more/privacy-policy";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }
    private void initComponents(){
        userGuide = findViewById(R.id.user_guide);
        themeSwitch = findViewById(R.id.theme);
        back = findViewById(R.id.back_button);
        profileImg = findViewById(R.id.profile_img);
        name = findViewById(R.id.name_layout);
        email = findViewById(R.id.email_layout);
        password = findViewById(R.id.password_layout);
        phone = findViewById(R.id.phone_layout);
        doctorCheck = findViewById(R.id.doctor);
        checkBox = findViewById(R.id.checkbox);
        signupBtn = findViewById(R.id.signup_button);
        progressIndicator = findViewById(R.id.progress);
        loading = findViewById(R.id.loading_txt);
        code = findViewById(R.id.code_layout);
        nameHint = findViewById(R.id.name_hint);
        emailHint = findViewById(R.id.email_hint);
        phoneHint = findViewById(R.id.phone_hint);
        passwordHint = findViewById(R.id.password_hint);
        termsOfService = findViewById(R.id.terms_of_service);
        privacyPolicy = findViewById(R.id.privacy_policy);
    }
    private boolean nameValidation(TextInputLayout name){
        boolean isValid = false;
        if(name.getEditText().getText().toString().isEmpty()){
            name.setErrorEnabled(true);
            nameHint.setVisibility(View.INVISIBLE);
            name.setError(getString(R.string.name_is_mandatory));
        }
        else if(name.getEditText().getText().toString().charAt(0) < 65 ||
                name.getEditText().getText().toString().charAt(0) > 122 ||
                (name.getEditText().getText().toString().charAt(0) > 90 && name.getEditText().getText().toString().charAt(0) < 97 )) {
            name.setErrorEnabled(true);
            nameHint.setVisibility(View.INVISIBLE);
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
                    name.setErrorEnabled(false);
                    nameHint.setVisibility(View.VISIBLE);
                    name.setError(null);
                }
                if((!s.toString().isEmpty() && s.toString().charAt(0) >= 65 && s.toString().charAt(0) <= 90 ) ||
                        (!s.toString().isEmpty() && s.toString().charAt(0) >= 97 && s.toString().charAt(0) <= 122 )){
                    name.setErrorEnabled(false);
                    nameHint.setVisibility(View.VISIBLE);
                    name.setError(null);
                }
            }
        });
        if(name.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private String sanitizeName(String name){
        StringBuilder sanitized = new StringBuilder();
        for(int i = 0 ; i < name.length(); i++){
            char ch = name.charAt(i);
            if(Character.isLetter(ch)|| ch ==' '){
                sanitized.append(ch);
            }
        }
        return sanitized.toString();
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
    private boolean phoneValidation(TextInputLayout phone){
        boolean isValid = false;
        if (phone.getEditText().getText().toString().isEmpty()){
            phone.setErrorEnabled(true);
            phoneHint.setVisibility(View.INVISIBLE);
            phone.setError("Phone is mandatory");
        }
        else if(phone.getEditText().getText().toString().length() != 11){
            phone.setErrorEnabled(true);
            phoneHint.setVisibility(View.INVISIBLE);
            phone.setError("Wrong phone number");
        }
        phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    phone.setErrorEnabled(false);
                    phoneHint.setVisibility(View.VISIBLE);
                    phone.setError(null);
                }
                else if(s.toString().length() == 11){
                    phone.setErrorEnabled(false);
                    phoneHint.setVisibility(View.VISIBLE);
                    phone.setError(null);
                }
            }
        });
        if(phone.getError() == null){
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
    private void createUser(AuthResult authResult ,String name ,String email ,String password ,Uri uri,String phone) throws Exception {
        String id = authResult.getUser().getUid();
        String type = getUserType(doctorCheck);
        UserModel user;
        DoctorModel doctor;
        String data = "";
        if(type.equals(Constant.PATIENT_TYPE)){
            user = new UserModel(id,name,email,type,uri.toString(),phone);
            data = security.encrypt(user);
        }else if (type.equals(Constant.DOCTOR_TYPE)){
            doctor = new DoctorModel(id,name,email,type,uri.toString(),phone,"Dentist","4,5","750m","A Dentist");
            data = security.encrypt(doctor);
        }
        FirebaseService.getFirebaseDatabase()
                .getReference(getUserType(doctorCheck))
                .child(id)
                .setValue(data)
                .addOnSuccessListener(
                        e -> {
                            progressIndicator.setProgress(100,true);
                            progressIndicator.setVisibility(View.INVISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                            signupBtn.setClickable(true);
                            LayoutInflater layoutInflater = LayoutInflater.from(SignupActivity.this);
                            View layout = layoutInflater.inflate(R.layout.alert_dialog_signup,null);
                            MaterialButton login = layout.findViewById(R.id.login_button);
                            AlertDialog dialog = new MaterialAlertDialogBuilder(SignupActivity.this).create();
                            login.setOnClickListener( v -> {
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password",password);
                                startActivity(intent);
                                finish();
                            });
                            dialog.setView(layout);
                            dialog.show();
                        }
                )
                .addOnFailureListener(e -> {
                    signupBtn.setClickable(true);
                    loading.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, "Couldn't save user info", Toast.LENGTH_SHORT).show();
                });
    }
    private void signup(String name , String email , String password , String phone){
        signupBtn.setClickable(false);
        loading.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);
        progressIndicator.setProgress(10,true);
        FirebaseService.getFirebaseAuth()
                .createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    progressIndicator.setProgress(30,true);
                    uploadProfileImg(authResult,name,email,password,uri.get(),phone);
                })
                .addOnFailureListener(e -> {
                    signupBtn.setClickable(true);
                    loading.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, "Sorry, Couldn't create account\nPlease try again", Toast.LENGTH_SHORT).show();
                });
    }
    private void uploadProfileImg(AuthResult authResult,String name,String email,String password,Uri imageUri,String phone){
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
                                try {
                                    createUser(authResult,name,email,password,uri,phone);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .addOnFailureListener(e -> {
                                signupBtn.setClickable(true);
                                loading.setVisibility(View.INVISIBLE);
                                progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                                progressIndicator.setProgress(100,true);
                                progressIndicator.setVisibility(View.INVISIBLE);
                                progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                                Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    signupBtn.setClickable(true);
                    loading.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void secondFactorVerify(AuthResult authResult,String phone,String email,String password){
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        progressIndicator.setProgress(70,true);
                        Intent intent = new Intent(getApplicationContext(), VerifySignUpCodeActivity.class);
                        intent.putExtra("id",verificationId);
                        intent.putExtra("phone",phone);
                        intent.putExtra("email",email);
                        intent.putExtra("password",password);
                        startActivity(intent);
                    }
                };
        authResult.getUser().getMultiFactor().getSession()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                MultiFactorSession multiFactorSession = task.getResult();
                                PhoneAuthOptions phoneAuthOptions =
                                        PhoneAuthOptions.newBuilder()
                                                .setPhoneNumber(phone)
                                                .setTimeout(30L, TimeUnit.SECONDS)
                                                .setMultiFactorSession(multiFactorSession)
                                                .setCallbacks(callbacks)
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
                            }
                        });
    }
}